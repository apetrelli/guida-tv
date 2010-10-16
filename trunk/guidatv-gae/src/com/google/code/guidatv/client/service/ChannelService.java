package com.google.code.guidatv.client.service;

import java.util.List;
import java.util.Set;

import com.google.code.guidatv.client.model.Channel;
import com.google.code.guidatv.client.model.Region;

public interface ChannelService {
    
    List<Region> getRegions();
    
    List<String> getNetworks(String regionCode);
    
    List<Channel> getChannels(String network);
    
    Channel getChannelByCode(String code);
    
    Set<String> getDefaultSelectedChannels();
}
