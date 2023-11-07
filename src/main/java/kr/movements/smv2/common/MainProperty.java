package kr.movements.smv2.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @file name : MainProperty.java
 * @project : spring-boot-init
 * @date : Nov 29, 2022
 * @author : ckr
 * @history:
 * @program comment : 프로젝트 전역에서 사용할 static properties
 */
@Component
@PropertySource("classpath:property.properties")
public class MainProperty {

	@Value("${image.thumbnail.width}")
	public int thumbnailWidth;

	@Value("${image.thumbnail.height}")
	public int thumbnailHeight;
}