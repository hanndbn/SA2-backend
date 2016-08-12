package com.tvi.common;

import com.tvi.common.entity.CVBagEntity;
import com.tvi.common.entity.CVCommentEntity;
import com.tvi.common.entity.CVEntityV2;
import com.tvi.common.entity.InfoEntity;
import com.tvi.common.entity.RequestEntityV2;
import com.tvi.common.entity.TestEntity;
import java.util.List;

public interface IRequestMgtV2 {
    
    long countOpenRequest(String k);
    
    List<RequestEntityV2> listOpenRequest(int p, int ps, String k, String[] s);
    
    /*tạo một request mới trong unit, trả về id của request vừa tạo*/

    long createRequest(int unit, RequestEntityV2 request);

    /*sửa nội dung của request*/
    void editRequest(long requestid, RequestEntityV2 entity);

    void deleteRequest(long requestid);

    RequestEntityV2 getRequest(long requestid);

    /*chú ý, đã thay String s bằng String[] s*/
    List<RequestEntityV2> listByUnit(int unit, int p, int ps, String[] s);

    /*trả về số lượng request trong unit*/
    long countRequestByUnit(int unit);

    CVEntityV2 getCV(long cvid);

    long createCV(long bagid, CVEntityV2 cv);

    void editCV(long cvid, CVEntityV2 cv);

    void deleteCV(long cvid);

    void commentCV(long cvid, CVCommentEntity comment);

    List<CVCommentEntity> listCVComment(long cvid);

    List<InfoEntity> listCVInfo(long cvid);

    long addCVInfo(long cvid, InfoEntity info);

    void addCVTest(long cvid, long testid);

    void addCVAnswer(long cvid, long answerid);

    long getCVTest(long cvid, int type);

    long getCVAnswer(long cvid, int type);

    List<TestEntity> listTest(long cvid);

    /*liệt kê toàn bộ cv trong một thùng*/
    List<CVEntityV2> listCVInBag(long bagid, int p, int ps, String[] s);

    /*liệt kê toàn bộ cv trong bag*/
    List<CVEntityV2> listCV(long bagid, int p, int ps, String[] s);

    /*chuyển cv vào thùng*/
    void moveCVintoBag(long cvid, long bagid);

    /*BAG*/
    List<CVBagEntity> listBag(long requestid);

    long createBag(long requestid, CVBagEntity bag);

    void editBag(long bagid, CVBagEntity bag);

    void deleteBag(long bagid);

    CVBagEntity getBag(long bagid);

    /* trả về unit của cv*/
    int getCVsUnit(long cvid);

    int getRequestsUnit(long requestid);

    int getBagsUnit(long bagid);

    long getCVFromTest(long testid);

    long getCVFromAnswer(long answerid);

    /*trả về requestid của cv*/
    long getRequestFromCV(long cvid);

    long getRequestFromBag(long bagid);

    long countCV(long bagid);

    String loadCVFile(long cvid);

    void storeCVFile(long cvid, String data);

    long getPrimaryBag(long requestid);

    long getSecondaryBag(long requestid);

    long getBagFromCV(long cvid);
}
