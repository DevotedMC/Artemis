package com.github.maxopoly.artemis.rabbit;

import com.github.maxopoly.artemis.ArtemisPlugin;
import com.github.maxopoly.zeus.rabbit.RabbitMessage;

public abstract class MCRabbitMessage extends RabbitMessage {

	protected MCRabbitMessage() {
		super(ArtemisPlugin.getInstance().getTransactionIdManager().pullNewTicket());
	}

}
