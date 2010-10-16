package com.google.code.guidatv.client.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.code.guidatv.client.model.Channel;
import com.google.code.guidatv.client.model.Region;
import com.google.code.guidatv.client.service.ChannelService;

public class ChannelServiceImpl implements ChannelService{
    
    private List<Region> regions;
    
    private Map<String, List<String>> region2networks;
    
    private Map<String, List<Channel>> network2channels;
    
    private Map<String, Channel> code2channel;
    
    private Set<String> defaultSelectedChannels;
    
    public ChannelServiceImpl() {
        regions = new ArrayList<Region>();
        regions.add(new Region("it_IT", "Italia"));
        region2networks = new LinkedHashMap<String, List<String>>();
        List<String> networks = new ArrayList<String>();
        network2channels = new LinkedHashMap<String, List<Channel>>();
        region2networks.put("it_IT", networks);
        addRaiChannels(networks);
        code2channel = new LinkedHashMap<String, Channel>();
        for (List<Channel> chns: network2channels.values()) {
            for (Channel channel: chns) {
                code2channel.put(channel.getCode(), channel);
            }
        }
        defaultSelectedChannels = new HashSet<String>();
        defaultSelectedChannels.add("RaiUno");
        defaultSelectedChannels.add("RaiDue");
        defaultSelectedChannels.add("RaiTre");
    }

    @Override
    public List<Region> getRegions() {
        return regions;
    }

    @Override
    public List<String> getNetworks(String regionCode) {
        return region2networks.get(regionCode);
    }

    @Override
    public List<Channel> getChannels(String network) {
        return network2channels.get(network);
    }

    @Override
    public Channel getChannelByCode(String code) {
        return code2channel.get(code);
    }

    @Override
    public Set<String> getDefaultSelectedChannels() {
        return defaultSelectedChannels;
    }

    private void addRaiChannels(List<String> networks) {
        networks.add("Rai");
        List<Channel> channels = new ArrayList<Channel>();
        channels.add(new Channel("RaiUno", "Rai 1", "generalistic", "it_IT"));
        channels.add(new Channel("RaiDue", "Rai 2", "generalistic", "it_IT"));
        channels.add(new Channel("RaiTre", "Rai 3", "generalistic", "it_IT"));
        channels.add(new Channel("Rai4", "Rai 4", "movies/series", "it_IT"));
        channels.add(new Channel("RaiNews", "Rai News 24", "news", "it_IT"));
        channels.add(new Channel("RaiSport1", "Rai Sport 1", "sport", "it_IT"));
        channels.add(new Channel("RaiSport2", "Rai Sport 2", "sport", "it_IT"));
        channels.add(new Channel("RaiEducational", "Rai Scuola", "education", "it_IT"));
        channels.add(new Channel("RaiEDU2", "Rai Storia", "education", "it_IT"));
        channels.add(new Channel("Extra", "Rai Extra", "retakes", "it_IT"));
        channels.add(new Channel("Premium", "Rai Premium", "retakes", "it_IT"));
        channels.add(new Channel("CinemaWorld", "Rai Movie", "movies", "it_IT"));
        channels.add(new Channel("RaiGulp", "Rai Gulp", "children", "it_IT"));
        channels.add(new Channel("Yoyo", "Rai Yoyo", "children", "it_IT"));
        channels.add(new Channel("EuroNews", "Euronews", "news", "it_IT"));
        network2channels.put("Rai", channels);
    }

}
