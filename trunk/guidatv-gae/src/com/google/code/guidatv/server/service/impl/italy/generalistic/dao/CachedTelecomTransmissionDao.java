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
import com.google.code.guidatv.client.model.Transmission;

public class CachedTelecomTransmissionDao implements TelecomTransmissionDao {

    private TelecomTransmissionDao dao;

    private Cache cache;

    public CachedTelecomTransmissionDao(TelecomTransmissionDao dao) {
        this.dao = dao;
        Map<Integer, Object> props = new HashMap<Integer, Object>();
        props.put(GCacheFactory.EXPIRATION_DELTA, 12 * 60 * 60);

        try {
            CacheFactory cacheFactory = CacheManager.getInstance()
                    .getCacheFactory();
            cache = cacheFactory.createCache(props);
        } catch (CacheException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, List<Transmission>> getTransmissions(Date day) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        String key = "Telecom" + format.format(day);
        Map<String, List<Transmission>> retValue = (Map<String, List<Transmission>>) cache.get(key);
        if (retValue == null) {
            retValue = dao.getTransmissions(day);
            cache.put(key, retValue);
        }
        return retValue;
    }

}
