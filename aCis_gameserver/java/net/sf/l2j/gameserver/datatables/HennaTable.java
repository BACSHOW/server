package net.sf.l2j.gameserver.datatables;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.model.item.Henna;
import net.sf.l2j.gameserver.templates.StatsSet;
import net.sf.l2j.gameserver.xmlfactory.XMLDocumentFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class HennaTable
{
	private static Logger _log = Logger.getLogger(HennaTable.class.getName());
	
	private final Map<Integer, Henna> _henna = new HashMap<>();
	private final Map<Integer, List<Henna>> _hennaTrees = new HashMap<>();
	
	protected HennaTable()
	{
		try
		{
			final File f = new File("./data/xml/henna.xml");
			final StatsSet set = new StatsSet();
			
			final Document doc = XMLDocumentFactory.getInstance().loadDocument(f);
			
			final Node n = doc.getFirstChild();
			for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
			{
				if (!d.getNodeName().equalsIgnoreCase("henna"))
					continue;
				
				final int id = Integer.valueOf(d.getAttributes().getNamedItem("symbol_id").getNodeValue());
				
				set.set("symbol_id", id);
				
				set.set("dye", Integer.valueOf(d.getAttributes().getNamedItem("dye_id").getNodeValue()));
				set.set("price", Integer.valueOf(d.getAttributes().getNamedItem("price").getNodeValue()));
				
				set.set("INT", Integer.valueOf(d.getAttributes().getNamedItem("INT").getNodeValue()));
				set.set("STR", Integer.valueOf(d.getAttributes().getNamedItem("STR").getNodeValue()));
				set.set("CON", Integer.valueOf(d.getAttributes().getNamedItem("CON").getNodeValue()));
				set.set("MEN", Integer.valueOf(d.getAttributes().getNamedItem("MEN").getNodeValue()));
				set.set("DEX", Integer.valueOf(d.getAttributes().getNamedItem("DEX").getNodeValue()));
				set.set("WIT", Integer.valueOf(d.getAttributes().getNamedItem("WIT").getNodeValue()));
				
				final Henna template = new Henna(set);
				_henna.put(id, template);
				
				final String[] classes = d.getAttributes().getNamedItem("classes").getNodeValue().split(",");
				for (String clas : classes)
				{
					final Integer classId = Integer.valueOf(clas);
					if (!_hennaTrees.containsKey(classId))
					{
						List<Henna> list = new ArrayList<>();
						list.add(template);
						_hennaTrees.put(classId, list);
					}
					else
						_hennaTrees.get(classId).add(template);
				}
				set.clear();
			}
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "HennaTable: Error loading from database:" + e.getMessage(), e);
		}
		_log.config("HennaTable: Loaded " + _henna.size() + " templates.");
	}
	
	public Henna getTemplate(int id)
	{
		return _henna.get(id);
	}
	
	public List<Henna> getAvailableHenna(int classId)
	{
		final List<Henna> henna = _hennaTrees.get(classId);
		if (henna == null)
			return Collections.emptyList();
		
		return henna;
	}
	
	public static HennaTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final HennaTable _instance = new HennaTable();
	}
}