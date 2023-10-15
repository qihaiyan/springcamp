package cn.springcamp.springdata.envers;

import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversPostUpdateEventListenerImpl;
import org.hibernate.event.spi.PostUpdateEvent;

public class MyEnversPostUpdateEventListenerImpl extends EnversPostUpdateEventListenerImpl {

    public MyEnversPostUpdateEventListenerImpl(EnversService enversService) {
        super(enversService);
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if (event.getEntity() instanceof MyData && ((MyData) event.getEntity()).getAuthor() == null) {
            return;
        }

        super.onPostUpdate(event);
    }
}
