package com.censoredsoftware.infractions.bukkit.legacy.data;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ServerData extends DataAccess<UUID, ServerData>
{
	private UUID id;
	private ServerDataType type;
	private String row;
	private String column;
	private Object value;
	private Long expiration;

	public ServerData()
	{
	}

	@Register(idType = IdType.UUID)
	public ServerData(UUID id, ConfigurationSection conf)
	{
		this.id = id;
		type = ServerDataType.valueOf(conf.getString("type"));
		row = conf.getString("row");
		column = conf.getString("column");
		value = conf.get("value");
		if(expiration != null) expiration = conf.getLong("expiration");
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", type.name());
		map.put("row", row);
		map.put("column", column);
		map.put("value", value);
		if(expiration != null) map.put("expiration", expiration);
		return map;
	}

	public void generateId()
	{
		id = UUID.randomUUID();
	}

	public void setType(ServerDataType type)
	{
		this.type = type;
	}

	public void setRow(String key)
	{
		this.row = key;
	}

	public void setColumn(String subKey)
	{
		this.column = subKey;
	}

	public void setValue(Object data)
	{
		if(data instanceof String || data instanceof Integer || data instanceof Boolean || data instanceof Double || data instanceof Map || data instanceof List) this.value = data;
		else if(data == null) this.value = "null";
		else this.value = data.toString();
	}

	public void setExpiration(TimeUnit unit, long time)
	{
		this.expiration = System.currentTimeMillis() + unit.toMillis(time);
	}

	public UUID getId()
	{
		return id;
	}

	public ServerDataType getType()
	{
		return type;
	}

	public String getRow()
	{
		return row;
	}

	public String getColumn()
	{
		return column;
	}

	public Object getValue()
	{
		return value;
	}

	public Long getExpiration()
	{
		return expiration;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if(this == obj) return true;
		if(obj == null || getClass() != obj.getClass()) return false;
		final ServerData other = (ServerData) obj;
		return Objects.equal(this.id, other.id) && Objects.equal(this.row, other.row) && Objects.equal(this.column, other.column) && Objects.equal(this.value, other.value);
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(this.id, this.row, this.column, this.value);
	}

	@Override
	public String toString()
	{
		return Objects.toStringHelper(this).add("id", this.id).add("row", this.row).add("subkey", this.column).add("value", this.value).toString();
	}

	private static final DataAccess<UUID, ServerData> DATA_ACCESS = new ServerData();

	public static Collection<ServerData> all()
	{
		return DATA_ACCESS.allDirect();
	}

	/*
	 * Persistent value
     */
	public static void put(String row, String column, Object value)
	{
		// Remove the value if it exists already
		remove(row, column);

		// Create and save the timed value
		ServerData timedData = new ServerData();
		timedData.generateId();
		timedData.setType(ServerDataType.PERSISTENT);
		timedData.setRow(row);
		timedData.setColumn(column);
		timedData.setValue(value);
		timedData.save();
	}

	/*
	 * Timed value
	 */
	public static void put(String row, String column, Object value, long time, TimeUnit unit)
	{
		// Remove the value if it exists already
		remove(row, column);

		// Create and save the timed value
		ServerData timedData = new ServerData();
		timedData.generateId();
		timedData.setType(ServerDataType.TIMED);
		timedData.setRow(row);
		timedData.setColumn(column);
		timedData.setValue(value);
		timedData.setExpiration(unit, time);
		timedData.save();
	}

	public static boolean exists(String row, String column)
	{
		return find(row, column) != null;
	}

	public static Object get(String row, String column)
	{
		return find(row, column).getValue();
	}

	public static Long getExpiration(String row, String column) throws NullPointerException
	{
		return find(row, column).getExpiration();
	}

	public static ServerData find(String row, String column)
	{
		if(findByRow(row) == null) return null;

		for(ServerData data : findByRow(row))
			if(data.getColumn().equals(column)) return data;

		throw new NullPointerException("Cannot find timed value at (row: " + row + ", column: " + column + ").");
	}

	public static Set<ServerData> findByRow(final String row)
	{
		return Sets.newHashSet(Collections2.filter(all(), new Predicate<ServerData>()
		{
			@Override
			public boolean apply(ServerData serverData)
			{
				return serverData.getRow().equals(row);
			}
		}));
	}

	public static void remove(String row, String column)
	{
		if(find(row, column) != null) find(row, column).remove();
	}

	/**
	 * Clears all expired timed value.
	 */
	public static void clearExpired()
	{
		for(ServerData data : Collections2.filter(all(), new Predicate<ServerData>()
		{
			@Override
			public boolean apply(ServerData data)
			{
				return ServerDataType.TIMED.equals(data.getType()) && data.getExpiration() <= System.currentTimeMillis();
			}
		}))
			data.remove();
	}
}
