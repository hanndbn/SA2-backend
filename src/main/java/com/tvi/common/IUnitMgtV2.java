package com.tvi.common;


import com.tvi.common.entity.RoleEntity;
import com.tvi.common.entity.UnitEntity;
import com.tvi.common.entity.UserEntityV2;
import com.tvi.common.type.AccessDeny;
import java.util.List;

public interface IUnitMgtV2
{

	/* tạo một user mới với username và password tương ứng, trả về id*/
	long createUP(String username, String password, UserEntityV2 user);

	/* sua username va password cho user*/
	void editUP(long uid, String username, String password);

	/* trả về id của user có username và password tương ứng*/
	long matchUP(String username, String password);

	UserEntityV2 getUser(long userid);

	/* sửa thông tin của user có id là userid*/
	void editUser(long userid, UserEntityV2 user);

	/* thiết lập password mới cho user, trả về password đó*/
	String resetPassword(long userid);

	/* liệt kê toàn bộ role của user*/
	List<RoleEntity> listRolebyUser(long userid);

	/*liệt kê toàn bộ unit của user*/
	List<UnitEntity> listUnitbyUser(long userid);
	
	/*thêm vào danh sách unit của user*/
	void getInUnit(long userid, int unit);
	
	/*xóa unit từ danh sách unit của user*/
	void getOutUnit(long userid, int unit);
	
	/*kiểm tra quyền thực hiện action của user, nếu ko dc thực hiện, tung exception*/
	void access(long userid, String action) throws AccessDeny, RuntimeException;
	
	String getConfig(int unitid, String ref);
	
	void setConfig(int unitid, String ref, String val);
	
	//lấy username của người dùng, không có, trả về null (ko phải xâu rỗng)
	String getUsername(long userid);
        
        UnitEntity getUnit(int id);
}
