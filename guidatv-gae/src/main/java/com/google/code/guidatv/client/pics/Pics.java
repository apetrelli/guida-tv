package com.google.code.guidatv.client.pics;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Pics extends ClientBundle {

    @Source("home.png")
    ImageResource home();

    @Source("info.png")
    ImageResource info();

    @Source("google.png")
    ImageResource google();

    @Source("wikipedia.png")
    ImageResource wikipedia();

    @Source("imdb.png")
    ImageResource imdb();

    @Source("loading.gif")
    ImageResource loading();
}
