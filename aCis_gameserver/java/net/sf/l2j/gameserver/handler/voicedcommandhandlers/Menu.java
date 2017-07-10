package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.instance.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class Menu implements IVoicedCommandHandler
{
    private static final String[] _voicedCommands =
    {
        "menu",
        "setPartyRefuse",
        "setTradeRefuse",    
        "setbuffsRefuse",
        "setMessageRefuse",
        "setxpRefuse",
    };
    
    private static final String ACTIVED = "<font color=00FF00>ON</font>";
    private static final String DESATIVED = "<font color=FF0000>OFF</font>";
    
    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target)
    {
        if (command.equals("menu"))
            showHtml(activeChar);        
                
        else if (command.equals("setPartyRefuse"))
        {
            if (activeChar.isPartyInRefuse())
                activeChar.setIsPartyInRefuse(false);
            else
                activeChar.setIsPartyInRefuse(true);            
            showHtml(activeChar);
        }    
        else if (command.equals("setTradeRefuse"))
        {
            if (activeChar.getTradeRefusal())
                activeChar.setTradeRefusal(false);
            else
                activeChar.setTradeRefusal(true);
            showHtml(activeChar);
        }        
        else if (command.equals("setMessageRefuse"))
        {        
            if (activeChar.isInRefusalMode())
                activeChar.setInRefusalMode(false);
            else
                activeChar.setInRefusalMode(true);
            showHtml(activeChar);
        }
        else if (command.equals("setbuffsRefuse"))
        {        
            if (activeChar.isBuffProtected())
                activeChar.setIsBuffProtected(false);
            else
                activeChar.setIsBuffProtected(true);
            showHtml(activeChar);
        }
        else if (command.equals("setxpRefuse"))
        {        
            if (activeChar.cantGainXP())
                activeChar.SetcantGainXP(false);
            else
                activeChar.SetcantGainXP(true);
            showHtml(activeChar);
        }
        return true;
    }
    
    private static void showHtml(Player activeChar)
    {
        NpcHtmlMessage html = new NpcHtmlMessage(0);
        html.setFile("data/html/mods/menu.htm");
        html.replace("%online%", World.getInstance().getPlayers().size());    
        html.replace("%partyRefusal%", activeChar.isPartyInRefuse() ? ACTIVED : DESATIVED);
        html.replace("%tradeRefusal%", activeChar.getTradeRefusal() ? ACTIVED : DESATIVED);
        html.replace("%buffsRefusal%", activeChar.isBuffProtected() ? ACTIVED : DESATIVED);
        html.replace("%messageRefusal%", activeChar.isInRefusalMode() ? ACTIVED : DESATIVED);    
        html.replace("%xpRefusal%", activeChar.cantGainXP() ? ACTIVED : DESATIVED);  
        activeChar.sendPacket(html);
    }
    
    @Override
    public String[] getVoicedCommandList()
    {
        return _voicedCommands;
    }
}