package test.accelerate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import accelerate.databean.AccelerateDataBean;
import accelerate.util.FileUtil;
import accelerate.util.StringUtil;

/**
 * Basic class to quick test code
 *
 * @author Rohit Narayanan
 * @version 1.0 Initial Version
 * @since 25-May-2015
 */
@SuppressWarnings("all")
public class QuickTest {
	public static void main(String[] args) {
		try {
			System.out.println(StringUtil.split("a,b,c,d", ",").stream()
					.flatMap(val -> Stream.of(
							AccelerateDataBean.build("key", val, "inKey", "inKey-1-" + val, "val", "val-" + val),
							AccelerateDataBean.build("key", val, "inKey", "inKey-2-" + val, "val", "val-" + val)))
					.collect(Collectors.groupingBy(bean -> bean.get("key").toString(), () -> new HashMap<>(), Collectors
							.toMap(bean -> bean.get("inKey").toString(), bean -> bean.get("val").toString()))));
		} catch (Exception error) {
			error.printStackTrace();
		}
	}
}
