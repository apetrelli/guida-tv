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

public class CachedIrisTransmissionDao implements IrisTransmissionDao {

    private IrisTransmissionDaoImpl dao;
    
    private Cache cache;
    
    public CachedIrisTransmissionDao() {
        dao = new IrisTransmissionDaoImpl();
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
    public List<Transmission> getTransmissions(Date day) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        String key = "Iris" + format.format(day);
        List<Transmission> retValue = (List<Transmission>) cache.get(key);
        if (retValue == null) {
            Map<Date, List<Transmission>> transmissionMap = dao.getTransmissions(day);
            if (transmissionMap != null) {
                for (Map.Entry<Date, List<Transmission>> entry : transmissionMap.entrySet()) {
                    cache.put("Iris" + format.format(entry.getKey()), entry.getValue());
                }
            }
            retValue = (List<Transmission>) cache.get(key);
        }
        return retValue;
    }
}
