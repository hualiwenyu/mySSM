package com.stephen.ssm.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 
 * @ClassName: JsonUtil
 * @Description: json和object互相转换工具类
 * @author stephen
 * @date 2016年9月21日 下午5:10:23
 *
 */
public class JsonUtil {

	private static final Logger LOG = LoggerFactory.getLogger(JsonUtil.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public static <T> T toObject(String string, Class<T> clazz) {
		try {
			return mapper.readValue(string, clazz);
		} catch (IOException e) {
			LOG.error("Error when toObject", e);
		}
		return null;
	}

	public static String toJson(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (IOException e) {
			LOG.error("Error when toJson", e);
		}
		return null;
	}
}
