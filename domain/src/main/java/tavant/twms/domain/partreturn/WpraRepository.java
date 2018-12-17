package tavant.twms.domain.partreturn;

import tavant.twms.infra.GenericRepository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: deepak.patel
 * Date: 2/12/12
 * Time: 2:42 PM
 * To change this template use File | Settings | File Templates.
 */
public interface WpraRepository extends GenericRepository<Wpra, Long> {

     void reloadWpras(List<Wpra> wpras);

	Wpra findLastFiledWpra();

}
