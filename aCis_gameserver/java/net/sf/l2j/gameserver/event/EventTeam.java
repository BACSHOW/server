/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.event;

public class EventTeam implements Comparable<EventTeam>
{
	private int score;
	private String name;
	private int[] nameColor;
	private int[] startPos;
	private int id;
	
	public EventTeam(int id, String name, int[] color, int[] startPos)
	{
		this.id = id;
		this.name = name;
		this.startPos = startPos;
		score = 0;
		nameColor = color;
	}
	
	@Override
	public int compareTo(EventTeam second)
	{
		if (getScore() > second.getScore())
			return 1;
		else if (getScore() < second.getScore())
			return -1;
		
		return 0;
	}
	
	protected String getHexaColor()
	{
		return (nameColor[0] > 15 ? Integer.toHexString(nameColor[0]) : "0" + Integer.toHexString(nameColor[0])) + (nameColor[1] > 15 ? Integer.toHexString(nameColor[1]) : "0" + Integer.toHexString(nameColor[1])) + (nameColor[2] > 15 ? Integer.toHexString(nameColor[2]) : "0" + Integer.toHexString(nameColor[2]));
	}
	
	protected int getId()
	{
		return id;
	}
	
	protected String getName()
	{
		return name;
	}
	
	protected int getScore()
	{
		return score;
	}
	
	protected int[] getTeamColor()
	{
		return nameColor;
	}
	
	protected int[] getTeamPos()
	{
		return startPos;
	}
	
	protected void increaseScore()
	{
		score++;
	}
	
	protected void increaseScore(int ammount)
	{
		score += ammount;
	}
	
	protected void setScore(int ammount)
	{
		score = ammount;
	}
}