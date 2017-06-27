package net.sf.l2j.gameserver.model.actor.ai;

public class IntentionCommand
{
	private final CtrlIntention _crtlIntention;
	private final Object _arg0, _arg1;
	
	public IntentionCommand(CtrlIntention pIntention, Object pArg0, Object pArg1)
	{
		_crtlIntention = pIntention;
		_arg0 = pArg0;
		_arg1 = pArg1;
	}
	
	public CtrlIntention getCtrlIntention()
	{
		return _crtlIntention;
	}
	
	public Object getFirstArgument()
	{
		return _arg0;
	}
	
	public Object getSecondArgument()
	{
		return _arg1;
	}
}