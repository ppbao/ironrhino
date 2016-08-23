package org.ironrhino.core.sequence.simple;

import org.ironrhino.core.spring.configuration.PrioritizedQualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

public class RedisSimpleSequence extends AbstractSimpleSequence {

	public static final String KEY_SEQUENCE = "seq:";

	@Autowired
	@Qualifier("stringRedisTemplate")
	@PrioritizedQualifier("sequenceStringRedisTemplate")
	private RedisTemplate<String, String> stringRedisTemplate;

	private BoundValueOperations<String, String> boundValueOperations;

	@Override
	public void afterPropertiesSet() {
		Assert.hasText(getSequenceName());
		Assert.isTrue(getPaddingLength() > 0);
		boundValueOperations = stringRedisTemplate.boundValueOps(KEY_SEQUENCE + getSequenceName());
		boundValueOperations.setIfAbsent("0");
	}

	@Override
	public void restart() {
		boundValueOperations.set("0");
	}

	@Override
	public int nextIntValue() {
		return boundValueOperations.increment(1).intValue();
	}

}