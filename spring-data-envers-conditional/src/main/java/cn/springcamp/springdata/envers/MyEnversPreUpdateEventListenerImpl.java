package cn.springcamp.springdata.envers;

import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.event.spi.EnversPreUpdateEventListenerImpl;
import org.hibernate.event.spi.PreUpdateEvent;

public class MyEnversPreUpdateEventListenerImpl extends EnversPreUpdateEventListenerImpl {

    public MyEnversPreUpdateEventListenerImpl(EnversService enversService) {
        super(enversService);
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        if (event.getEntity() instanceof MyData
                && ((MyData) event.getEntity()).getAuthor() == null) {
            return false;
        }

        return super.onPreUpdate(event);
    }

}
