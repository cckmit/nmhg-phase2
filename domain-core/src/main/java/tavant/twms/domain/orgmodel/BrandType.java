package tavant.twms.domain.orgmodel;

import java.io.Serializable;

import org.springframework.core.style.ToStringCreator;

public enum BrandType implements Serializable{
		HYSTER("HYSTER"), YALE("YALE"), UTILEV("UTILEV");

		private String type;

		private BrandType(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		@Override
		public String toString() {
			return new ToStringCreator(this).append("type", type).toString();
		}
}
