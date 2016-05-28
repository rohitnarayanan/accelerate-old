package accelerate.spring;

import static accelerate.util.AccelerateConstants.COMMA_CHAR;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;

import accelerate.cache.AccelerateCache;
import accelerate.databean.AccelerateDataBean;
import accelerate.exception.AccelerateException;
import accelerate.logging.AuditLoggerAspect;
import accelerate.logging.Auditable;
import accelerate.util.AppUtil;
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
	 *             thrown due to {@link #initializeContextListenerMap()} and
	 *             {@link #initializeCacheListenerMap()}
	 * 
	 */
	@PostConstruct
	public void initialize() throws AccelerateException {
		Exception exception = null;
		StopWatch stopWatch = AuditLoggerAspect.logMethodStart("accelerate.spring.StaticListenerHelper.construct()");
		try {
			initializeContextListenerMap();
			initializeCacheListenerMap();
		} catch (AccelerateException error) {
			exception = error;
			throw error;
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
	 *             thrown due to
	 *             {@link #getAnnotationAttributes(BeanDefinition)}
	 */
	private void initializeContextListenerMap() throws AccelerateException {
		this.staticContextListeners = findCandidateComponents(StaticContextListener.class).stream()
				.flatMap(beanDefinition -> {
					AnnotationAttributes annotationAttributes = getAnnotationAttributes(beanDefinition);
					return Stream.of(
							AccelerateDataBean.build("event", "onContextStarted", "listenerClass",
									beanDefinition.getBeanClassName(), "handleMethod",
									annotationAttributes.getString("onContextStarted")),
							AccelerateDataBean.build("event", "onContextClosed", "listenerClass",
									beanDefinition.getBeanClassName(), "handleMethod",
									annotationAttributes.getString("onContextClosed")));
				}).collect(Collectors.groupingBy(bean -> bean.get("event").toString(), () -> new HashMap<>(), Collectors
						.toMap(bean -> bean.get("listenerClass").toString(), bean -> bean.get("handler").toString())));
	}

	/**
	 * @throws AccelerateException
	 *             thrown due to
	 *             {@link #getAnnotationAttributes(BeanDefinition)}
	 */
	private void initializeCacheListenerMap() throws AccelerateException {
		this.staticCacheListeners = findCandidateComponents(StaticCacheListener.class).stream().map(beanDefinition -> {
			AnnotationAttributes annotationAttributes = getAnnotationAttributes(beanDefinition);
			return AccelerateDataBean.build("cacheName", annotationAttributes.getString("name"), "listenerClass",
					beanDefinition.getBeanClassName(), "handleMethod", annotationAttributes.getString("handler"));
		}).collect(Collectors.groupingBy(bean -> bean.get("cacheName").toString(), () -> new HashMap<>(), Collectors
				.toMap(bean -> bean.get("listenerClass").toString(), bean -> bean.get("handler").toString())));
	}

	/**
	 * @param aAnnotationType
	 * @return
	 */
	private Set<BeanDefinition> findCandidateComponents(Class<? extends Annotation> aAnnotationType) {
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(aAnnotationType));

		Set<BeanDefinition> componentSet = new HashSet<>(provider.findCandidateComponents("accelerate.*"));
		StringUtil.split(this.accelerateProperties.getAppBasePackage(), COMMA_CHAR).parallelStream()
				.filter(packageName -> !AppUtil.compare(packageName, "accelerate"))
				.forEach(packageName -> componentSet.addAll(provider.findCandidateComponents(packageName)));

		return componentSet;
	}

	/**
	 * @param aBeanDefinition
	 * @return
	 * @throws AccelerateException
	 *             thrown due to
	 *             {@link ReflectionUtil#getFieldValue(Class, Object, String)}
	 */
	private static AnnotationAttributes getAnnotationAttributes(BeanDefinition aBeanDefinition)
			throws AccelerateException {
		Object metadata = ReflectionUtil.getFieldValue(aBeanDefinition.getClass(), aBeanDefinition, "metadata");
		@SuppressWarnings("unchecked")
		Map<String, LinkedList<AnnotationAttributes>> attributesMap = (Map<String, LinkedList<AnnotationAttributes>>) ReflectionUtil
				.getFieldValue(metadata.getClass(), metadata, "attributesMap");
		return attributesMap.get(StaticCacheListener.class.getName()).get(0);
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
				AccelerateException.checkAndThrow(error);
			}
		});
	}

	/**
	 * @param aCache
	 */
	@Auditable
	public void notifyCacheLoad(AccelerateCache<?, ?> aCache) {
		Map<String, String> listenerMap = this.staticCacheListeners.get(aCache.name());
		if (ObjectUtils.isEmpty(listenerMap)) {
			return;
		}

		listenerMap.forEach((aClassName, aHandlerName) -> {
			try {
				Class<?> targetClass = Class.forName(aClassName);
				ReflectionUtil.invokeMethod(targetClass, null, aHandlerName, new Class<?>[] { aCache.getClass() },
						new Object[] { aCache });
			} catch (Exception error) {
				AccelerateException.checkAndThrow(error);
			}
		});
	}
}