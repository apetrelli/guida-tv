package com.google.code.guidatv.server.service.impl.italy.generalistic.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import com.google.appengine.api.memcache.stdimpl.GCacheFactory;
import com.google.code.guidatv.model.Transmission;

public class CachedMediasetBaseTransmissionDao implements
        MediasetBaseTransmissionDao {

    private MediasetBaseTransmissionDao dao;
    private Cache cache;
    
    private static final String KEY = "MEDIASET_BASE_KEY";

    public CachedMediasetBaseTransmissionDao(MediasetBaseTransmissionDao dao) {
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
    public Map<Date, Map<String, List<Transmission>>> getTransmissions() {
        Map<Date, Map<String, List<Transmission>>> retValue = (Map<Date, Map<String, List<Transmission>>>) cache
                .get(KEY);
        if (retValue == null) {
            retValue = dao.getTransmissions();
            cache.put(KEY, retValue);
        }
        return retValue;
    }

}
