/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tvi.common.manager;

import com.tvi.common.IDatabase;
import com.tvi.common.IRequestMgtV2;
import com.tvi.common.entity.CVBagEntity;
import com.tvi.common.entity.CVCommentEntity;
import com.tvi.common.entity.CVEntityV2;
import com.tvi.common.entity.InfoEntity;
import com.tvi.common.entity.RequestEntityV2;
import com.tvi.common.entity.TestEntity;
import com.tvi.data.daos.BagDAOV2;
import com.tvi.data.daos.CVDAOV2;
import com.tvi.data.daos.CVFileDAOV2;
import com.tvi.data.daos.CVTestAnswerDAOV2;
import com.tvi.data.daos.CVTestDAOV2;
import com.tvi.data.daos.CommentDAOV2;
import com.tvi.data.daos.InfoDAOV2;
import com.tvi.data.daos.RatingDAOV2;
import com.tvi.data.daos.RequestDAOV2;
import com.tvi.data.daos.TestAnswerDAOV2;
import com.tvi.data.daos.TestDAOV2;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Manh
 */
public class RequestMgrV2 implements IRequestMgtV2 {

    RequestDAOV2 requestdao = new RequestDAOV2();
    CVDAOV2 cvdao = new CVDAOV2();
    RatingDAOV2 ratingdao = new RatingDAOV2();
    CommentDAOV2 commentdao = new CommentDAOV2();
    InfoDAOV2 infodao = new InfoDAOV2();
    CVTestDAOV2 cvtestdao = new CVTestDAOV2();
    CVTestAnswerDAOV2 cvtestanswerv2dao = new CVTestAnswerDAOV2();
    BagDAOV2 bagdao = new BagDAOV2();
    TestDAOV2 testdao = new TestDAOV2();
    TestAnswerDAOV2 testAnserdao = new TestAnswerDAOV2();
    CVFileDAOV2 cvfiledao = new CVFileDAOV2();

    public RequestMgrV2(IDatabase database) {
        requestdao.setDatabase(database);
        cvdao.setDatabase(database);
        ratingdao.setDatabase(database);
        commentdao.setDatabase(database);
        infodao.setDatabase(database);
        cvtestdao.setDatabase(database);
        cvtestanswerv2dao.setDatabase(database);
        bagdao.setDatabase(database);
        testdao.setDatabase(database);
        testAnserdao.setDatabase(database);
        cvfiledao.setDatabase(database);
    }

    @Override
    public long createRequest(int unit, RequestEntityV2 request) {
        return requestdao.add(unit, request);
    }

    @Override
    public void editRequest(long requestid, RequestEntityV2 entity) {
        requestdao.update(requestid, entity);
    }

    @Override
    public void deleteRequest(long requestid) {
        requestdao.delete(requestid);
    }

    @Override
    public RequestEntityV2 getRequest(long requestid) {
        return requestdao.getRequest(requestid);
    }

    @Override
    public List<RequestEntityV2> listByUnit(int unit, int p, int ps, String[] s) {
        return requestdao.listByUnit(unit, p, ps, s);
    }

    @Override
    public long countRequestByUnit(int unit) {
        return requestdao.countRequestByUnit(unit);
    }

    @Override
    public CVEntityV2 getCV(long cvid) {
        return cvdao.getCVEntity(cvid);
    }

    @Override
    public void editCV(long cvid, CVEntityV2 cv) {
        cvdao.update(cvid, cv);
    }

    @Override
    public void deleteCV(long cvid) {
        cvdao.detele(cvid);
    }

    @Override
    public void commentCV(long cvid, CVCommentEntity comment) {
        commentdao.add(cvid, comment);
    }

    @Override
    public List<CVCommentEntity> listCVComment(long cvid) {
        return commentdao.getAllCommentByUser(cvid);
    }

    @Override
    public List<InfoEntity> listCVInfo(long cvid) {
        return infodao.listInfobyCv(cvid);
    }

    @Override
    public long addCVInfo(long cvid, InfoEntity info) {
        return infodao.add(cvid, info);
    }

    @Override
    public void addCVTest(long cvid, long testid) {
        cvtestdao.add(cvid, testid);
    }

    @Override
    public void addCVAnswer(long cvid, long answerid) {
        cvtestanswerv2dao.add(cvid, answerid);
    }

    @Override
    public long getCVTest(long cvid, int type) {
        return testdao.getTestID(cvid, type);
    }

    @Override
    public long getCVAnswer(long cvid, int type) {
        return testAnserdao.getTestAnswer(cvid, type);
    }

    @Override
    public List<TestEntity> listTest(long cvid) {
        return testdao.getListTest(cvid);
    }

    @Override
    public List<CVEntityV2> listCVInBag(long bagid, int p, int ps, String[] s) {
        return cvdao.getCVInBag(bagid, p, ps, s);
    }

    @Override
    public List<CVEntityV2> listCV(long requestid, int p, int ps, String[] s) {
        return cvdao.getCV(requestid, p, ps, s);
    }

    @Override
    public void moveCVintoBag(long cvid, long bagid) {
        cvdao.update(cvid, bagid);
    }

    @Override
    public List<CVBagEntity> listBag(long requestid) {
        return bagdao.listBagInRequest(requestid);
    }

    @Override
    public long createBag(long requestid, CVBagEntity bag) {
        return bagdao.add(requestid, bag);
    }

    @Override
    public void editBag(long bagid, CVBagEntity bag) {
        bagdao.update(bagid, bag);
    }

    @Override
    public void deleteBag(long bagid) {
        bagdao.delete(bagid);
    }

    @Override
    public CVBagEntity getBag(long bagid) {
        return bagdao.getBag(bagid);
    }

    @Override
    public int getRequestsUnit(long requestunit) {
        return requestdao.getUnit(requestunit);

    }

    @Override
    public int getBagsUnit(long bagid) {
        return bagdao.getUnit(bagid);
    }

    @Override
    public int getCVsUnit(long cvid) {
        return cvdao.getUnit(cvid);
    }

    @Override
    public long createCV(long bagid, CVEntityV2 cv) {
        return cvdao.add(bagid, cv);
    }

    @Override
    public long getCVFromTest(long testid) {
        return cvtestdao.getCV(testid);
    }

    @Override
    public long getCVFromAnswer(long answerid) {
        return cvtestanswerv2dao.getCV(answerid);
    }

    @Override
    public long getRequestFromCV(long cvid) {
        return cvdao.getRequest(cvid);
    }

    @Override
    public long getRequestFromBag(long bagid) {
        return bagdao.getRequest(bagid);
    }

    @Override
    public long countCV(long bagid) {
        return cvdao.countCVinBag(bagid);
    }

    @Override
    public String loadCVFile(long cvid) {
        return cvfiledao.getFile(cvid);
    }

    @Override
    public void storeCVFile(long cvid, String data) {
        cvfiledao.add(cvid, data);
    }

    @Override
    public long getPrimaryBag(long request) {
        return bagdao.getFist(request);
    }

    @Override
    public long getSecondaryBag(long reques) {
        return bagdao.getSecond(reques);
    }

    @Override
    public long getBagFromCV(long cvid) {
        return cvdao.getBagId(cvid);
    }

    @Override
    public long countOpenRequest(String k) {
        return requestdao.countOpenRequest(k);
    }

    @Override
    public List<RequestEntityV2> listOpenRequest(int p, int ps, String k, String[] s) {
        try {
            return requestdao.listOpenRequest(p, ps, k, s);
        } catch (SQLException ex) {
            Logger.getLogger(RequestMgrV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
