package tavant.twms.domain.bu;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.springframework.util.StringUtils;

public class BusinessUnitInfoType implements UserType {
	private static final int[] SQL_TYPES = new int[] { Types.VARCHAR };

	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return cached;
	}

	public Object deepCopy(Object value) throws HibernateException {
		if (value == null) {
			return null;
		}
		BusinessUnitInfo original = (BusinessUnitInfo) value;
		BusinessUnitInfo result = new BusinessUnitInfo();
		result.setName(original.getName());
		return result;
	}

	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		
		if ((x == null && y == null)||
				(x!=null && ((BusinessUnitInfo) x).getName() == null && y!=null && ((BusinessUnitInfo) y).getName() == null)) {
			return true;
		}
		
		if((x==null && y!=null)||(x!=null && y==null))
		{
			return false;
		}

		return ((BusinessUnitInfo) x).getName().equalsIgnoreCase(
				(((BusinessUnitInfo) y).getName()));
	}

	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	public boolean isMutable() {
		return false;
	}

	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
			throws HibernateException, SQLException {
		BusinessUnitInfo buAudit = new BusinessUnitInfo();
		buAudit.setName(rs.getString(names[0]));
		return buAudit;
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index)
			throws HibernateException, SQLException 
	{		
		if (value != null) 
		{
			Object[] valueAsArray = null;
			if (value instanceof String) 
			{				
				st.setString(index, (String)value);
			}		
			else if(value instanceof Object[])
			{				
				valueAsArray = (Object[])value;	
//				valueAsArray = new String[tempArray.length];
//				String canICodeBetter = "";
//				for(int i=0;i<tempArray.length;i++)
//				{					
//					valueAsArray[i] = ((BusinessUnitInfo)tempArray[i]).getName();
//					canICodeBetter = canICodeBetter + "'" + valueAsArray[i] + "'";
//					if(i < (tempArray.length - 1) )
//					{
//						canICodeBetter = canICodeBetter + ",";
//					}
//				}
				st.setString(index, StringUtils.arrayToCommaDelimitedString(valueAsArray));
			}
			else if (value instanceof BusinessUnitInfo)
			{
				BusinessUnitInfo buAudit = (BusinessUnitInfo) value;
				st.setString(index, buAudit == null ? null : buAudit.getName());
			}					
		}
		else
			st.setString(index,null);
	}

	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return null;
	}

	public Class returnedClass() {
		return BusinessUnitInfo.class;
	}

	public int[] sqlTypes() {
		return SQL_TYPES;
	}

}
