package accelerate.spring;

import static accelerate.util.AccelerateConstants.COMMA_CHAR;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import accelerate.cache.AccelerateCache;
import accelerate.exception.AccelerateException;
import accelerate.exception.AccelerateRuntimeException;
import accelerate.logging.AuditLoggerAspect;
import accelerate.logging.Auditable;
import accelerate.util.AppUtil;
import accelerate.util.CollectionUtil;
import accelerate.util.ReflectionUtil;
import accelerate.util.StringUtil;

/**
 * Utility class that handles Spring context load event.
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since Dec 28, 2009
 */
@Component
@Profile("accelerate")
public class StaticListenerHelper implements ApplicationListener<ApplicationReadyEvent>, Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * List of classes annotated with @AccelerateContextListener. Static
	 * reference is stores to avoid scanning classpath multiple times as it is
	 * an expensive operation
	 */
	private Map<String, Map<String, String>> staticContextListeners = null;

	/**
	 * List of classes annotated with @StaticCacheListener. Static reference is
	 * stores to avoid scanning classpath multiple times as it is an expensive
	 * operation
	 */
	private Map<String, Map<String, String>> staticCacheListeners = null;

	/**
	 * static {@link ApplicationContext} instance, to provide spring beans
	 * access to all classes.
	 */
	@Autowired
	private transient ApplicationContext applicationContext = null;

	/**
	 * {@link AccelerateProperties} instance
	 */
	@Autowired
	private AccelerateProperties accelerateProperties = null;

	/**
	 * @throws AccelerateException
	 * 
	 */
	@PostConstruct
	public void construct() throws AccelerateException {
		Exception exception = null;
		StopWatch stopWatch = AuditLoggerAspect.logMethodStart("accelerate.spring.StaticListenerHelper.construct()");
		try {
			initializeContextListenerMap();
			initializeCacheListenerMap();
		} catch (Exception error) {
			exception = error;
			AccelerateException.checkAndThrow(error);
		} finally {
			AuditLoggerAspect.logMethodEnd("accelerate.spring.StaticListenerHelper.construct()", exception, stopWatch);
		}
	}

	/**
	 * This method is called to notify that the application is shutting down or
	 * the context has been destroyed.
	 */
	@PreDestroy
	public void destroy() {
		notifyContextListener("onContextClosed");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.context.ApplicationListener#onApplicationEvent(org.
	 * springframework.context.ApplicationEvent)
	 */
	/**
	 * This method initializes Accelerate components to register that a new
	 * {@link BeanFactory} has been initialized. It notifies any custom
	 * implementation of {@link StaticContextListener} provided by the
	 * application. It then notifies any static classes that maybe registered as
	 * a listener for spring context initialization or {@link AccelerateCache}
	 * reload event.
	 * 
	 * @param aEvent
	 */
	@Override
	@Auditable
	public void onApplicationEvent(ApplicationReadyEvent aEvent) {
		notifyContextListener("onContextStarted");
	}

	/**
	 * @throws AccelerateException
	 */
	private void initializeContextListenerMap() throws AccelerateException {
		this.staticContextListeners = new HashMap<>();
		Set<BeanDefinition> contextListeners = findCandidateComponents(StaticContextListener.class);

		for (BeanDefinition beanDefinition : contextListeners) {
			Object metadata = ReflectionUtil.getFieldValue(beanDefinition.getClass(), beanDefinition, "metadata");
			@SuppressWarnings("unchecked")
			Map<String, LinkedList<AnnotationAttributes>> attributesMap = (Map<String, LinkedList<AnnotationAttributes>>) ReflectionUtil
					.getFieldValue(metadata.getClass(), metadata, "attributesMap");
			AnnotationAttributes annotationAttributes = attributesMap.get(StaticContextListener.class.getName()).get(0);

			CollectionUtil.addToValueMap(this.staticContextListeners, beanDefinition.getBeanClassName(),
					"onContextStarted", annotationAttributes.getString("onContextStarted"));
			CollectionUtil.addToValueMap(this.staticContextListeners, beanDefinition.getBeanClassName(),
					"onContextClosed", annotationAttributes.getString("onContextClosed"));
		}
	}

	/**
	 * @throws AccelerateException
	 */
	private void initializeCacheListenerMap() throws AccelerateException {
		this.staticCacheListeners = new HashMap<>();
		Set<BeanDefinition> cacheListeners = findCandidateComponents(StaticCacheListener.class);

		for (BeanDefinition beanDefinition : cacheListeners) {
			Object metadata = ReflectionUtil.getFieldValue(beanDefinition.getClass(), beanDefinition, "metadata");
			@SuppressWarnings("unchecked")
			Map<String, LinkedList<AnnotationAttributes>> attributesMap = (Map<String, LinkedList<AnnotationAttributes>>) ReflectionUtil
					.getFieldValue(metadata.getClass(), metadata, "attributesMap");
			AnnotationAttributes annotationAttributes = attributesMap.get(StaticCacheListener.class.getName()).get(0);

			CollectionUtil.addToValueMap(this.staticCacheListeners, annotationAttributes.getString("name"),
					beanDefinition.getBeanClassName(), annotationAttributes.getString("handler"));
		}
	}

	/**
	 * @param aAnnotationType
	 * @return
	 */
	private Set<BeanDefinition> findCandidateComponents(Class<? extends Annotation> aAnnotationType) {
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(aAnnotationType));

		Set<BeanDefinition> componentSet = new HashSet<>(provider.findCandidateComponents("accelerate.*"));
		String[] packages = StringUtil.split(this.accelerateProperties.getAppBasePackage(), COMMA_CHAR);
		for (String packageStr : packages) {
			if (!AppUtil.compare(packageStr, "accelerate")) {
				componentSet.addAll(provider.findCandidateComponents(packageStr));
			}
		}

		return componentSet;
	}

	/**
	 * @param aHandlerKey
	 */
	private void notifyContextListener(String aHandlerKey) {
		this.staticContextListeners.forEach((aClassName, aHandlerMap) -> {
			try {
				Class<?> targetClass = Class.forName(aClassName);
				ReflectionUtil.invokeMethod(targetClass, null, aHandlerMap.get(aHandlerKey),
						new Class<?>[] { ApplicationContext.class }, new Object[] { this.applicationContext });
			} catch (Exception error) {
				throw new AccelerateRuntimeException(error);
			}
		});
	}

	/**
	 * @param aCache
	 */
	@Auditable
	public void notifyCacheLoad(AccelerateCache<?, ?> aCache) {
		Map<String, String> listenerMap = this.staticCacheListeners.get(aCache.name());
		if (AppUtil.isEmpty(listenerMap)) {
			return;
		}

		listenerMap.forEach((aClassName, aHandlerName) -> {
			try {
				Class<?> targetClass = Class.forName(aClassName);
				ReflectionUtil.invokeMethod(targetClass, null, aHandlerName, new Class<?>[] { aCache.getClass() },
						new Object[] { aCache });
			} catch (Exception error) {
				throw new AccelerateRuntimeException(error);
			}
		});
	}
}