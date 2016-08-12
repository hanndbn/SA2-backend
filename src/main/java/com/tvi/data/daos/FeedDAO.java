//package com.tvi.data.daos;
//
//import com.tvi.common.IDatabase;
//import com.tvi.common.config.ConfigCount;
////import com.tvi.common.entity.FeedEntity;
//import com.tvi.data.entity.Feedofuser;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// *
// * @author CAFEITVN.COM
// */
//public class FeedDAO {
//
//    private IDatabase database;
//
//    private boolean readed;
//
//    private long fid;
//
//    private String title;
//
//    private Date ctime;
//
//    private String content;
//
//    private int state;
//
//    private long userid;
//
//    public void setDatabase(IDatabase database) {
//        this.database = database;
//    }
//
////    private boolean blockFeed(long feedId_in) {
////        boolean sucess = false;
////        try {
////            String strSQL = "UPDATE Feed SET state=?  WHERE fid=? ";
////            PreparedStatement stmt = database.prepareStatement(strSQL);
////            stmt.setLong(2, feedId_in);
////            stmt.setInt(1, ConfigCount.DELETED);
////            stmt.execute();
////            stmt.close();
////            sucess = true;
////        } catch (SQLException ex) {
////            Logger.getLogger(FeedDAO.class.getName()).log(Level.SEVERE, null, ex);
////        }
////        return sucess;
////    }
////
////    private boolean unblockFeed(long feedId_in) {
////        boolean sucess = false;
////        try {
////            String strSQL = "UPDATE Feed SET state=?  WHERE fid=? ";
////            PreparedStatement stmt = database.prepareStatement(strSQL);
////            stmt.setLong(2, feedId_in);
////            stmt.setInt(1, ConfigCount.EXIST);
////            stmt.execute();
////            stmt.close();
////            sucess = true;
////        } catch (SQLException ex) {
////            Logger.getLogger(FeedDAO.class.getName()).log(Level.SEVERE, null, ex);
////        }
////        return sucess;
////    }
////
////    public boolean blockFeedFollowUser(long userId_in) {
////        boolean sucess = false;
//////        try {
////        // Start delete feedTable
////        List<Feedofuser> allFeedOfUser = getAllFeedOfUser(userId_in);
////        for (int i = 0; i < allFeedOfUser.size(); i++) {
////            blockFeed(allFeedOfUser.get(i).fid);
////        }
////        // End delete feedTable       
////
//////            // Start dellete feedOfUserTable
//////            String sql = "DELETE FROM Feedofuser WHERE uid = ?";
//////            PreparedStatement stmt = database.prepareStatement(sql);;
//////            stmt.setLong(1, userId_in);
//////            stmt.executeUpdate(sql);
//////            stmt.close();
//////            // End delete tabel feedOfUserTable
////        sucess = true;
////
//////        } catch (SQLException ex) {
//////            Logger.getLogger(FeedDAO.class.getName()).log(Level.SEVERE, null, ex);
//////        }
////        return sucess;
////    }
////
////    public boolean unblockFeedFollowUser(long userId_in) {
////        boolean sucess = false;
////        List<Feedofuser> allFeedOfUser = getAllFeedOfUser(userId_in);
////        for (int i = 0; i < allFeedOfUser.size(); i++) {
////            unblockFeed(allFeedOfUser.get(i).fid);
////        }
////        sucess = true;
////        return sucess;
////    }
////
////    private List<Feedofuser> getAllFeedOfUser(long useriId_in) {
////        List<Feedofuser> listFeedofuser = new ArrayList<Feedofuser>();
////        try {
////            String str = "SELECT uid,fid,readed FROM Feedofuser WHERE uid = ? ";
////            PreparedStatement stmt = database.prepareStatement(str);
////            stmt.setLong(1, useriId_in);
////            ResultSet mRs = stmt.executeQuery();
////            while (mRs.next()) {
////                Feedofuser tempFeedofuser = new Feedofuser(mRs.getLong(0), mRs.getLong(1), mRs.getInt(2));
////                listFeedofuser.add(tempFeedofuser);
////            }
////            mRs.close();
////            stmt.close();
////        } catch (SQLException ex) {
////            Logger.getLogger(FeedDAO.class.getName()).log(Level.SEVERE, null, ex);
////        }
////        return listFeedofuser;
////    }
////
////    public FeedEntity getNewFeed(long userId_in, long nFeedId_in) {
////        FeedEntity tempFeed = null;
////        try {
////            String sql = "SELECT Feed.title,Feed.ctime,Feed.content,Feed.state,FeedOfUser.readed FROM Feed ,FeedOfUser WHERE Feed.fid = FeedOfUser.fid and Feed.fid = ? and FeedOfUser.uid = ?;";
////            PreparedStatement stmt = database.prepareStatement(sql);
////            stmt.setLong(1, nFeedId_in);
////            stmt.setLong(2, userId_in);
////            ResultSet mRs = stmt.executeQuery();
////            if (mRs.next()) {
////                fid = nFeedId_in;
////                userid = userId_in;
////                title = mRs.getString(0);
////                ctime = new java.util.Date(mRs.getDate(1).getTime());
////                content = mRs.getString(2);
////                state = mRs.getInt(3);
////                readed = false;
////                if (mRs.getInt(4) == ConfigCount.READED) {
////                    readed = true;
////                }
////                tempFeed = new FeedEntity(fid, title, ctime, content, state, readed, userid);
////            }
////            mRs.close();
////            stmt.close();
////
////        } catch (SQLException ex) {
////            Logger.getLogger(FeedDAO.class.getName()).log(Level.SEVERE, null, ex);
////        }
////        return tempFeed;
////    }
////
//    public boolean readNewFeed(long userId_in, long nFeedId_in) {
//        boolean sucess = false;
//        try {
//            String strSQL = "UPDATE Feedofuser SET readed=?  WHERE uid=? and fid=? ";
//            PreparedStatement stmt = database.prepareStatement(strSQL);
//            stmt.setLong(2, nFeedId_in);
//            stmt.setLong(3, nFeedId_in);
//            stmt.setInt(1, ConfigCount.READED);
//            stmt.execute();
//            stmt.close();
//            sucess = true;
//
//        } catch (SQLException ex) {
//            Logger.getLogger(FeedDAO.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return sucess;
//    }
////
////    public List<FeedEntity> getNewFeed(long useriId_in, int p_in, int ps_in) {
////        List<FeedEntity> listFeedofuser = new ArrayList<FeedEntity>();
////        try {
////            String str = "SELECT Feed.fid,Feed.title,Feed.ctime,Feed.content,Feed.state FROM Feed,FeedOfUser WHERE"
////                    + " Feed.fid = FeedOfUser.fid "
////                    + "and FeedOfUser.uid = ? "
////                    + "and Feed.state = ? "
////                    + "and FeedOfUser.readed = ? "
////                    + "and Feed.ctime = (SELECT max(ctime) from feed)  "
////                    + "limit ?,?";
////            PreparedStatement stmt = database.prepareStatement(str);
////            stmt.setLong(1, useriId_in);
////            stmt.setInt(2, ConfigCount.EXIST);
////            stmt.setInt(3, ConfigCount.NEW);
////            stmt.setInt(4, (p_in * ps_in));
////            stmt.setInt(5, ps_in);
////            ResultSet mRs = stmt.executeQuery();
////            while (mRs.next()) {
////                userid = useriId_in;
////                readed = false;
////                fid = mRs.getLong(0);
////                title = mRs.getString(1);
////                ctime = new java.util.Date(mRs.getDate(2).getTime());
////                content = mRs.getString(3);
////                state = mRs.getInt(4);
////                FeedEntity tempFeedofuser = new FeedEntity(fid, title, ctime, content, state, readed, userid);
////                listFeedofuser.add(tempFeedofuser);
////            }
////            mRs.close();
////            stmt.close();
////        } catch (SQLException ex) {
////            Logger.getLogger(FeedDAO.class.getName()).log(Level.SEVERE, null, ex);
////        }
////        return listFeedofuser;
////    }
////
////    private boolean add(FeedEntity feed_in) {
////        boolean sucess = false;
////        try {
////            String sql = "INSERT INTO Feed(title,ctime,content,state) VALUES(?,?,?,?);";
////            PreparedStatement stmt = database.prepareStatement(sql);
////            stmt.setString(1, feed_in.title);
////            stmt.setTimestamp(2, new java.sql.Timestamp(feed_in.ctime.getTime()));
////            stmt.setString(3, feed_in.content);
////            stmt.setInt(4, feed_in.state);
////            stmt.execute();
////            stmt.close();
////            sucess = true;
////        } catch (SQLException ex) {
////            Logger.getLogger(FeedDAO.class.getName()).log(Level.SEVERE, null, ex);
////        }
////        return sucess;
////    }
////
////    public boolean add(Long userId_in, FeedEntity feed_in) {
////        boolean sucess = false;
////        try {
////            add(feed_in);
////            String sql = "INSERT INTO FeedOfUser(uid , fid , readed ) VALUES (?,?,?);";
////            PreparedStatement stmt = database.prepareStatement(sql);
////            stmt.setLong(1, userId_in);
////            stmt.setLong(2, feed_in.fid);
////            stmt.setInt(3, ConfigCount.NEW);
////            sucess = true;
////        } catch (SQLException ex) {
////            Logger.getLogger(FeedDAO.class.getName()).log(Level.SEVERE, null, ex);
////        }
////        return sucess;
////    }
////
//}
