package org.async.net;

import java.nio.channels.SelectionKey;

public interface ClearableChannelProcessor extends ChannelProcessor{

	void clear(SelectionKey key);
}
