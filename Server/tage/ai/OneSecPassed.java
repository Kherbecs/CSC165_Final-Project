package tage;

import tage.ai.behaviortrees.BTCondition;
import tage.ai.*;

public class OneSecPassed extends BTCondition
{ 
	NPC npc;
	NPCcontroller npcc;
	GameAIServerUDP server;

	public OneSecPassed(GameAIServerUDP s, NPCcontroller c, NPC n, boolean toNegate)
	{ 
		super(toNegate);
		server = s; npcc = c; npc = n;
	}
	protected boolean check()
	{ 
		server.sendCheckForAvatarNear();
		return npcc.getNearFlag();
	}
}