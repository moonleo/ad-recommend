package db.bean;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class UserTypeList implements Serializable, UserType {
    private List children;
    private List parents;
    private static final String SPLITTER= "/";
    private static final int[] TYPES = new int[]{Types.VARCHAR};

    @Override
    public int[] sqlTypes() {
        return TYPES;
    }

    @Override
    public Class returnedClass() {
        return List.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if(x == y)
            return true;
        if(null != x && null != y) {
            List xList = (List)x;
            List yList = (List)y;

            if(xList.size() != yList.size())
                return false;

            for (int i = 0; i < xList.size(); i++) {
                String xStr = (String)xList.get(i);
                String yStr = (String)yList.get(i);
                if(!xStr.equals(yStr))
                    return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode(Object o) throws HibernateException {
        return 0;
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] strings, Object o) throws HibernateException, SQLException {
        String value = (String)Hibernate.STRING.nullSafeGet(resultSet, strings[0]);
        if (value != null) {
            return parse(value);
        } else {
            return null;
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int i) throws HibernateException, SQLException {
        //System.out.println("Set method excecuted");
        if (value != null) {
            String str = assemble((List)value);
            Hibernate.STRING.nullSafeSet(preparedStatement, str, i);
        } else {
            Hibernate.STRING.nullSafeSet(preparedStatement, value, i);
        }
    }

    private String assemble(List list) {
        if(list.size() == 0 || list == null)
            return "";
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < list.size() - 1; i++) {
            stringBuffer.append(list.get(i)).append(SPLITTER);
        }
        stringBuffer.append(list.get(list.size() - 1));
        return stringBuffer.toString();
    }

    @Override
    public Object deepCopy(Object o) throws HibernateException {
        List sourse = (List)o;
        List target = new ArrayList();
        if(null == sourse)
            return null;
        target.addAll(sourse);
        return target;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object o) throws HibernateException {
        return null;
    }

    @Override
    public Object assemble(Serializable serializable, Object o) throws HibernateException {
        return null;
    }

    @Override
    public Object replace(Object o, Object o2, Object o3) throws HibernateException {
        return null;
    }

    private List parse(String value) {
        String[] strs = StringUtils.split(value, SPLITTER);
        List list = new ArrayList();
        for (int i = 0; i < strs.length; i++) {
            list.add(strs[i]);
        }
        return list;
    }
}
