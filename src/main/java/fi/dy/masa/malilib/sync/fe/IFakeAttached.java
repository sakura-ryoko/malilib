package fi.dy.masa.malilib.sync.fe;

import org.jetbrains.annotations.Nullable;

public interface IFakeAttached
{
    void updateAttachmentPosition();

    boolean canStayAttached();

    void onBreak(@Nullable FakeEntity breaker);
}
