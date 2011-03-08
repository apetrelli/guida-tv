package com.google.code.guidatv.android;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
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

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.google.code.guidatv.model.Channel;
import com.google.code.guidatv.rest.ChannelsResource;

public class ChannelsView extends ListActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_list);
        Engine.getInstance().getRegisteredConverters().add(new JacksonConverter());
        ClientResource resource = new ClientResource("http://10.0.2.2:8888/rest/channels");
        Representation representation = resource.get(MediaType.APPLICATION_JSON);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibilityChecker(new VisibilityChecker.Std(
                Visibility.DEFAULT, Visibility.DEFAULT, Visibility.DEFAULT,
                Visibility.DEFAULT, Visibility.ANY));
        Reader reader;
        List<Channel> channels;
        try
        {
            reader = representation.getReader();
            channels = mapper.readValue(reader, new TypeReference<List<Channel>>() {});
            reader.close();
        } catch (IOException e)
        {
            Log.e("guida tv", "errore IO", e);
            throw new RuntimeException(e);
        }
        String[] channelArray = new String[channels.size()];
        int i = 0;
        for (Channel channel : channels) {
            channelArray[i] = channel.getName();
            i++;
        }
        ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.channel_item, R.id.channel_text, channelArray);
        setListAdapter(adapter);
    }
}
