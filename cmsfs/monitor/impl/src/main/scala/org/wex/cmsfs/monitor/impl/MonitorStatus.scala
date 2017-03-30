package org.wex.cmsfs.monitor.impl

import com.redis.RedisClient

class MonitorStatus {
  val redisClient = new RedisClient("redis.cmsfs.org", 6379)

}
