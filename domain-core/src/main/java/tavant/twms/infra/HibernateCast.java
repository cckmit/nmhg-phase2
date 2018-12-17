package tavant.twms.infra;

import org.hibernate.proxy.HibernateProxy;

public class HibernateCast<T> {

	@SuppressWarnings("unchecked")
	public T cast(Object o) {
		if (o != null && o instanceof HibernateProxy) {
			return (T) (((HibernateProxy) o).getHibernateLazyInitializer()
					.getImplementation());
		} else {
			return (T) o;
		}
	}
}
