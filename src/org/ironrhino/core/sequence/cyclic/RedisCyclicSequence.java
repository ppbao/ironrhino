package org.ironrhino.core.sequence.cyclic;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.ironrhino.core.spring.configuration.PrioritizedQualifier;
import org.ironrhino.core.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.util.Assert;

public class RedisCyclicSequence extends AbstractCyclicSequence {

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
		int maxlength = String.valueOf(Long.MAX_VALUE).length() - getCycleType().getPattern().length();
		Assert.isTrue(getPaddingLength() <= maxlength, "paddingLength should not large than " + maxlength);
		boundValueOperations = stringRedisTemplate.boundValueOps(KEY_SEQUENCE + getSequenceName());
		boundValueOperations.setIfAbsent(getStringValue(now(), getPaddingLength(), 0));
	}

	@Override
	public String nextStringValue() {
		List<Object> results = stringRedisTemplate.executePipelined(new RedisCallback<Object>() {
			@Override
			public List<Object> doInRedis(RedisConnection connection) throws DataAccessException {
				connection.incr(boundValueOperations.getKey().getBytes());
				connection.time();
				return null;
			}
		});
		long value = (Long) results.get(0);
		Date now = new Date((Long) results.get(1));
		final String stringValue = String.valueOf(value);
		if (stringValue.length() == getPaddingLength() + getCycleType().getPattern().length()) {
			Calendar cal = Calendar.getInstance();
			CycleType cycleType = getCycleType();
			if (cycleType.ordinal() <= CycleType.MINUTE.ordinal())
				cal.set(Calendar.MINUTE, Integer.valueOf(stringValue.substring(10, 12)));
			if (cycleType.ordinal() <= CycleType.HOUR.ordinal())
				cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(stringValue.substring(8, 10)));
			if (cycleType.ordinal() <= CycleType.DAY.ordinal())
				cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(stringValue.substring(6, 8)));
			if (cycleType.ordinal() <= CycleType.MONTH.ordinal())
				cal.set(Calendar.MONTH, Integer.valueOf(stringValue.substring(4, 6)) - 1);
			if (cycleType.ordinal() <= CycleType.YEAR.ordinal())
				cal.set(Calendar.YEAR, Integer.valueOf(stringValue.substring(0, 4)));
			Date d = cal.getTime();
			if (getCycleType().isSameCycle(d, now))
				return stringValue;
			else if (d.after(now)) {
				// overflow
				long next = value
						- Long.valueOf(DateUtils.formatDate8(now)) * ((long) Math.pow(10, getPaddingLength()));
				return DateUtils.format(now, cycleType.getPattern()) + next;
			}
		}
		final String restart = getStringValue(now, getPaddingLength(), 1);
		boolean success = stringRedisTemplate.execute(new SessionCallback<Boolean>() {
			@Override
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Boolean execute(RedisOperations operations) {
				operations.watch(Collections.singleton(boundValueOperations.getKey()));
				if (stringValue.equals(boundValueOperations.get())) {
					operations.multi();
					boundValueOperations.set(restart);
					return operations.exec() != null;
				} else {
					return false;
				}
			}
		});
		if (success)
			return restart;
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return nextStringValue();
	}

	protected Date now() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(stringRedisTemplate.execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.time();
			}
		}));
		return cal.getTime();
	}

}