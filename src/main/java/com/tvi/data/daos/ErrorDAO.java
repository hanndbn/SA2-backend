//package com.tvi.data.daos;
//
//import com.tvi.common.IDatabase;
//import com.tvi.common.config.ConfigCount;
//import com.tvi.common.entity.ErrorEntity;
//import com.tvi.data.entity.Errorofuser;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// *
// * @author Manh
// */
//public class ErrorDAO {
//
//    private IDatabase database;
//    private long userid;
//    private boolean readed;
//    private long eid;
//    private int state;
//    private long lid;
//    private String message;
//
//    public void setDatabase(IDatabase database) {
//        this.database = database;
//    }
//
//    private List<Errorofuser> getAllErrorOfUser(long userId_in) {
//        List listErrorofuser = new ArrayList<Errorofuser>();
//        try {
//            String sql = "SELECT eid,uid,readed FROM Errorofuser WHERE uid = ? ";
//            PreparedStatement stmt = database.prepareStatement(sql);
//            stmt.setLong(1, userId_in);
//            ResultSet mRs = stmt.executeQuery();
//            while (mRs.next()) {
//                Errorofuser tempErrorofuser = new Errorofuser(mRs.getLong(0), mRs.getLong(1), mRs.getInt(2));
//                listErrorofuser.add(tempErrorofuser);
//            }
//            mRs.close();
//            stmt.close();
//
//        } catch (SQLException ex) {
//            Logger.getLogger(ErrorDAO.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return listErrorofuser;
//    }
//
//    private boolean lockError(long errorId_in) {
//        boolean sucess = false;
//        try {
//            String sql = "UPDATE Error SET  state=?  WHERE eid=? ";
//            PreparedStatement stmt = database.prepareStatement(sql);
//            stmt.setInt(1, ConfigCount.DELETED);
//            stmt.setLong(2, errorId_in);
//            stmt.execute();
//            stmt.close();
//            sucess = true;
//        } catch (SQLException ex) {
//            Logger.getLogger(ErrorDAO.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return sucess;
//    }
//
//    private boolean unblockError(long errorId_in) {
//        boolean sucess = false;
//        try {
//            String sql = "UPDATE Error SET  state=?  WHERE eid=? ";
//            PreparedStatement stmt = database.prepareStatement(sql);
//            stmt.setInt(1, ConfigCount.EXIST);
//            stmt.setLong(2, errorId_in);
//            stmt.execute();
//            stmt.close();
//            sucess = true;
//        } catch (SQLException ex) {
//            Logger.getLogger(ErrorDAO.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return sucess;
//    }
//
//    public boolean blockErrorFollowUser(long userId_in) {
//        boolean sucess = false;
////        try {
//        // DONT DELETE LOG   
//        // Start delete errorTable
//        List<Errorofuser> allErrorOfUser = getAllErrorOfUser(userId_in);
//        for (int i = 0; i < allErrorOfUser.size(); i++) {
//            lockError(allErrorOfUser.get(i).eid);
//        }
//
//        // End delete errorTable
////            // Start delete errorOfUserTable
////            String sql = "DELETE FROM Errorofuser WHERE  uid = ?";
////            PreparedStatement stmt = database.prepareStatement(sql);
////            stmt.setLong(1, userId_in);
////            stmt.executeUpdate(sql);
////            stmt.close();
////            // End delete errorOfUserTable
//        sucess = true;
////        } catch (SQLException ex) {
////            Logger.getLogger(ErrorDAO.class.getName()).log(Level.SEVERE, null, ex);
////        }
//        return sucess;
//    }
//
//    public boolean unblockErrorFollowUser(long userId_in) {
//        boolean sucess = false;
//        List<Errorofuser> allErrorOfUser = getAllErrorOfUser(userId_in);
//        for (int i = 0; i < allErrorOfUser.size(); i++) {
//            unblockError(allErrorOfUser.get(i).eid);
//        }
//        sucess = true;
//        return sucess;
//
//    }
//
//    public boolean readNewError(long userId_in, long errId_in) {
//        boolean sucess = false;
//        try {
//            String strSQL = "UPDATE Errorofuser SET readed=?  WHERE eid=? and uid=? ";
//            PreparedStatement stmt = database.prepareStatement(strSQL);
//            stmt.setLong(2, errId_in);
//            stmt.setLong(3, userId_in);
//            stmt.setInt(1, ConfigCount.READED);
//            stmt.execute();
//            stmt.close();
//            sucess = true;
//
//        } catch (SQLException ex) {
//            Logger.getLogger(ErrorDAO.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return sucess;
//    }
//
//    public List<ErrorEntity> getNewError(long userId_in, int p, int ps) {
//        List<ErrorEntity> listErrorEntity = new ArrayList<ErrorEntity>();
//        ErrorEntity tempError = null;
//        try {
//            String sql = "SELECT Error.eid,Error.lid,Error.message FROM Error, ErrorOfUser WHERE"
//                    + " Error.eid = ErrorOfUser.edi "
//                    + "and Error.uid = ? "
//                    + "and ErrorOfUser.readed = ? "
//                    + "and Error.state = ? "
//                    + "limit ?,? ;";
//            PreparedStatement stmt = database.prepareStatement(sql);
//            stmt.setLong(1, userId_in);
//            stmt.setInt(2, ConfigCount.NEW);
//            stmt.setInt(3, ConfigCount.EXIST);
//            stmt.setInt(4, (p * ps));
//            stmt.setInt(5, ps);
//            ResultSet mRs = stmt.executeQuery();
//            while (mRs.next()) {
//                eid = mRs.getLong(0);
//                userid = userId_in;
//                lid = mRs.getLong(1);
//                state = ConfigCount.EXIST;
//                message = mRs.getString(2);
//                readed = false;
//                tempError = new ErrorEntity(eid, state, lid, message);
//                listErrorEntity.add(tempError);
//
//            }
//            mRs.close();
//            stmt.close();
//
//        } catch (SQLException ex) {
//            Logger.getLogger(ErrorDAO.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return listErrorEntity;
//
//    }
//
//    private long add(ErrorEntity error_in) {
//        long sucess = -1;
//        try {
//            String sql = "INSERT INTO Error(lid,state,message) VALUES(?,?,?);";
//            PreparedStatement stmt = database.prepareStatement(sql);
//            stmt.setLong(1, error_in.lid);
//            stmt.setInt(2, error_in.state);
//            stmt.setString(3, error_in.message);
//            stmt.execute();
//            String sql1 = "SELECT eid FROM error WHERE lid = ? and state = ? and message = ?";
//            PreparedStatement stmt1 = database.prepareStatement(sql);
//            stmt1.setLong(1, error_in.lid);
//            stmt1.setInt(2, error_in.state);
//            stmt1.setString(3, error_in.message);
//            stmt1.execute();
//            stmt1.close();
//            ResultSet mRs = stmt1.executeQuery();
//            if (mRs.next())
//                sucess= mRs.getLong(0);
//            stmt1.close();
//        } catch (SQLException ex) {
//            Logger.getLogger(ErrorDAO.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return sucess;
//
//    }
//    
//    public boolean add(long userId_in, ErrorEntity error_in){
//        boolean sucess = false;
//        try {
//            long errorId = this.add(error_in);
//            String sql = "INSERT INTO ErrorOfUser(uid,eid,readed) VALUES (?,?,?)";
//            PreparedStatement stmt = database.prepareStatement(sql);
//            stmt.setLong(1, userId_in);
//            stmt.setLong(2, errorId);
//            stmt.setInt(3, ConfigCount.NEW);
//            sucess = true;
//        } catch (SQLException ex) {
//            Logger.getLogger(ErrorDAO.class.getName()).log(Level.SEVERE, null, ex);
//        }       
//        
//        return  sucess;        
//    }
//
//}
