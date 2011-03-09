package com.google.code.guidatv.android.rest;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.introspect.VisibilityChecker;
import org.codehaus.jackson.type.TypeReference;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import android.util.Log;

import com.google.code.guidatv.model.Channel;

public class GuidaTvService
{

    private ClientResource channelsResource;
    private ObjectMapper channelsMapper;

    public GuidaTvService()
    {
        Engine.getInstance().getRegisteredConverters().add(new JacksonConverter());
        channelsResource = new ClientResource("http://10.0.2.2:8888/rest/channels");
        channelsMapper = new ObjectMapper();
        channelsMapper.setVisibilityChecker(new VisibilityChecker.Std(
                Visibility.DEFAULT, Visibility.DEFAULT, Visibility.DEFAULT,
                Visibility.DEFAULT, Visibility.ANY));
    }

    public List<Channel> getChannels()
    {
        Representation representation = channelsResource.get(MediaType.APPLICATION_JSON);
        Reader reader;
        List<Channel> channels;
        try
        {
            reader = representation.getReader();
            channels = channelsMapper.readValue(reader, new TypeReference<List<Channel>>() {});
            reader.close();
        } catch (IOException e)
        {
            Log.e("guida tv", "errore IO", e);
            throw new RuntimeException(e);
        }
        return channels;
    }

}
