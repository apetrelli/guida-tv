package com.google.code.guidatv.server.service.impl.italy.generalistic.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import com.google.appengine.api.memcache.stdimpl.GCacheFactory;
import com.google.code.guidatv.model.Channel;
import com.google.code.guidatv.model.Transmission;

public class CachedSOMGialloTransmissionDao implements SOMGialloTransmissionDao {

    private SOMGialloTransmissionDaoImpl dao;
    private Cache cache;

    public CachedSOMGialloTransmissionDao(SOMGialloTransmissionDaoImpl dao) {
        this.dao = dao;

        Map<Integer, Object> props = new HashMap<Integer, Object>();
        props.put(GCacheFactory.EXPIRATION_DELTA, 12*60*60);

        try {
            CacheFactory cacheFactory = CacheManager.getInstance()
                    .getCacheFactory();
            cache = cacheFactory.createCache(props);
        } catch (CacheException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Transmission> getTransmissions(Channel channel, Date day) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        String key = channel.getCode() + format.format(day);
        List<Transmission> transmissions = (List<Transmission>) cache.get(key);
        if (transmissions == null) {
        	Map<Date, List<Transmission>> transmissionMap = dao.getTransmissions(channel, day);
        	if (transmissionMap != null) {
        		transmissions = transmissionMap.get(day);
        	}
        	for (Map.Entry<Date, List<Transmission>> entry: transmissionMap.entrySet()) {
        		cache.put(channel.getCode() + format.format(entry.getKey()), entry.getValue());
        	}
        }
        return transmissions;
    }

}
