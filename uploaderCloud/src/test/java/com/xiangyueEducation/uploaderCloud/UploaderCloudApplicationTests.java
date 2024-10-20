package com.xiangyueEducation.uploaderCloud;


import com.xiangyueEducation.uploaderCloud.Utils.PathEnum;
import org.junit.jupiter.api.Test;


//@SpringBootTest
class UploaderCloudApplicationTests {

	@Test
	void contextLoads(){

		System.out.println(PathEnum.QRCodeDirPath.getPath());

	}

}
