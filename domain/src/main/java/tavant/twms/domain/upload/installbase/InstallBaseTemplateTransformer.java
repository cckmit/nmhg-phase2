package tavant.twms.domain.upload.installbase;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import tavant.twms.domain.upload.controller.TemplateTransformer;
import tavant.twms.domain.upload.controller.UploadManagement;
import tavant.twms.domain.upload.controller.UploadManagementMetaData;
import tavant.twms.domain.upload.controller.UploadStatusDetail;

public class InstallBaseTemplateTransformer extends TemplateTransformer {

	@Override
	public UploadStatusDetail transform(InputStream file, FileOutputStream out,
			long fileUploadMgtId, int maxRowsAllowed,
			UploadManagement currentDataUpload,List<UploadManagementMetaData> uploadManagementMetaDatas) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
