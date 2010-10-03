package com.google.code.guidatv.client.pics;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Pics extends ClientBundle {

    @Source("info.png")
    ImageResource info();

    @Source("google.png")
    ImageResource google();

    @Source("wikipedia.png")
    ImageResource wikipedia();

    @Source("imdb.png")
    ImageResource imdb();
}
