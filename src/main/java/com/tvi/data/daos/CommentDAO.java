/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tvi.data.daos;

import com.tvi.common.IDatabase;
import com.tvi.common.config.ConfigCount;
import com.tvi.common.entity.CVCommentEntity;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Manh
 */

public class CommentDAO {

    private IDatabase database;

    private long id;

    private Date ctime;

    private long author;

    private int state;

    private String comment;

    public void setDatabase(IDatabase database) {
        this.database = database;
    }

    public boolean lockComment(long commentId_in) {
        boolean sucess = false;
        try {
            String sql = "UPDATE comment SET state=?  WHERE cid=? ; ";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setInt(1, ConfigCount.DELETED);
            stmt.setLong(2, commentId_in);
            stmt.execute();
            stmt.close();
            sucess = true;
        } catch (SQLException ex) {
            Logger.getLogger(CommentDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }

    public List<Long> getAllCommenIdtByUser(long cVId_in) {
        List<Long> commentids = new ArrayList<Long>();
        long cid = -1;
        try {
            String sql = "Select cid From comment WHERE cvid = ? and state != ?";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, cVId_in);
            stmt.setInt(2, ConfigCount.DELETED);
            ResultSet mRs = stmt.executeQuery();
            while (mRs.next()) {
                cid = mRs.getLong(1);
                commentids.add(new Long(cid));
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(CommentDAOV2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return commentids;
    }

    public List<CVCommentEntity> getAllCommentByUser(long cVId_in) {
        List<CVCommentEntity> listComment = new ArrayList<CVCommentEntity>();
        try {
            String sql = "SELECT cid,ctime,author,state,comment FROM Comment WHERE cvid = ? and state != ?";
            PreparedStatement stmt = database.prepareStatement(sql);
            stmt.setLong(1, cVId_in);
            stmt.setInt(2, ConfigCount.DELETED);
            ResultSet mRs = stmt.executeQuery();
            while (mRs.next()) {
                id = mRs.getLong(1);
                ctime = new java.util.Date(mRs.getLong(2));
                author = mRs.getLong(3);
                state = mRs.getInt(4);
                comment = mRs.getString(5);
                listComment.add(new CVCommentEntity(id, ctime, author, state, comment));
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(CommentDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listComment;
    }

    public long add(long cvId_in, CVCommentEntity Comment_in) {
        long sucess = -1;
        try {
            String strSQL = "INSERT INTO Comment(cvid,ctime,author,state,comment) VALUES(?,?,?,?,?);";
            PreparedStatement stmt = database.preparedStatement(strSQL,Statement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, cvId_in);
            stmt.setLong(2, (new java.util.Date().getTime()));
            stmt.setLong(3, Comment_in.author);
            stmt.setInt(4, Comment_in.state);
            stmt.setString(5, Comment_in.comment);
            stmt.execute();

            ResultSet mRs = stmt.getGeneratedKeys();
            if (mRs.next()){
                sucess = mRs.getLong(1);
            }
            mRs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(CommentDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sucess;
    }
}
