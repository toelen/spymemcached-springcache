spymemcached-springcache is a Spring Cache abstraction implementation for spymemcached

Usage
-----

Usage is very similar to the standard spring cache abstractions

<bean id="memcachedClient" class="net.spy.memcached.spring.MemcachedClientFactoryBean">
  ...
</bean>

<bean id="cacheManager" class="org.springframework.cache.support.SimpleCacheManager">
  <property name="caches">
    <set>
      <bean class="net.spy.memcached.spring.cache.MemcachedCache">
          <property name="name" value="default">
          <property name="client" ref="memcachedClient">
          <property name="expiry" value="3600">
      </bean>
    </set>
  </property>
</bean>
