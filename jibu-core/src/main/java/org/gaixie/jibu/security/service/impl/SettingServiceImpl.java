package org.gaixie.jibu.security.service.impl;

import com.google.inject.Inject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.security.MD5;
import org.gaixie.jibu.security.dao.SettingDAO;
import org.gaixie.jibu.security.dao.UserDAO;
import org.gaixie.jibu.security.model.Setting;
import org.gaixie.jibu.security.model.User;
import org.gaixie.jibu.security.service.SettingService;
import org.gaixie.jibu.utils.ConnectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Setting 服务接口的默认实现。
 * <p>
 */
public class SettingServiceImpl implements SettingService {
    Logger logger = LoggerFactory.getLogger(SettingServiceImpl.class);
    private final SettingDAO settingDAO;
    private final UserDAO userDAO;

    /**
     * 使用 Guice 进行 DAO 的依赖注入。
     * <p>
     */
    @Inject
    public SettingServiceImpl(SettingDAO settingDAO, UserDAO userDAO) {
        this.settingDAO = settingDAO;
        this.userDAO = userDAO;
    }


    public Setting get(int id) {
        Connection conn = null;
        Setting setting = null;
        try {
            conn = ConnectionUtils.getConnection();
            setting = settingDAO.get(conn,id);
        } catch(SQLException e) {
            logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
        return setting;
    }

    public Setting get(String name, String value) {
        Connection conn = null;
        Setting setting = null;
        try {
            conn = ConnectionUtils.getConnection();
            setting = settingDAO.get(conn,name, value);
        } catch(SQLException e) {
            logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
        return setting;
    }

    public Setting getDefault(String name) {
        Connection conn = null;
        Setting setting = null;
        try {
            conn = ConnectionUtils.getConnection();
            setting = settingDAO.getDefault(conn,name);
        } catch(SQLException e) {
            logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
        return setting;
    }

    public Setting getByUsername(String name,String username) {
        Connection conn = null;
        Setting setting = null;
        try {
            conn = ConnectionUtils.getConnection();
            setting = settingDAO.getByUsername(conn,name,username);
        } catch(SQLException e) {
            logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
        return setting;
    }

    public void add(Setting setting) throws JibuException {
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
            settingDAO.save(conn,setting);
            DbUtils.commitAndClose(conn);
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            throw new JibuException(e.getMessage());
        }
    }

    public void update(Setting setting) throws JibuException {
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
            settingDAO.update(conn,setting);
            DbUtils.commitAndClose(conn);
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            throw new JibuException(e.getMessage());
        }
    }

    public void delete(Setting setting) throws JibuException {
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
            if (setting.getId() == null) {
                setting = settingDAO.get(conn
                                         ,setting.getName()
                                         ,setting.getValue());
            }
            settingDAO.delete(conn,setting);
            DbUtils.commitAndClose(conn);
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            throw new JibuException(e.getMessage());
        }
    }

    public void bind(int sid, String username) throws JibuException {
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
            // 得到之前选择的配置
            Setting setting = settingDAO.get(conn,sid);
            Setting oldSetting = settingDAO.getByUsername(conn
                                                          ,setting.getName()
                                                          ,username);
            User user = userDAO.get(conn,username);
            if(oldSetting !=null) {
                settingDAO.unbind(conn,oldSetting.getId(),user.getId());
            }

            // 如果选择的是一个默认配置，不用bind
            if(setting.getSortindex()!=0) {
                settingDAO.bind(conn,setting.getId(),user.getId());
            }
            DbUtils.commitAndClose(conn);
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            throw new JibuException(e.getMessage());
        }
    }

    public void unbind(int sid, String username) throws JibuException{
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
            User user = userDAO.get(conn,username);
            settingDAO.unbind(conn,sid,user.getId());
            DbUtils.commitAndClose(conn);
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            throw new JibuException(e.getMessage());
        }
    }

    public void updateMe(List<Integer> ids, User user) throws JibuException {
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
            if (null != user.getPassword()) {
                String cryptpassword = MD5.encodeString(user.getPassword(),null);
                user.setPassword(cryptpassword);
            }

            int uid = user.getId();
            userDAO.update(conn,user);
            settingDAO.unbindAll(conn,uid);
            Setting setting = null;
            for(Integer id:ids) {
                setting = settingDAO.get(conn,id);
                if(setting.getSortindex()!=0) {
                    settingDAO.bind(conn,id,uid);
                }
            }
            // 如果选择的是一个默认配置，不用bind
            DbUtils.commitAndClose(conn);
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            throw new JibuException(e.getMessage());
        }
    }

    public void reset(String username) throws JibuException{
        Connection conn = null;
        try {
            conn = ConnectionUtils.getConnection();
            User user = userDAO.get(conn,username);
            settingDAO.unbindAll(conn,user.getId());
            DbUtils.commitAndClose(conn);
        } catch(SQLException e) {
            DbUtils.rollbackAndCloseQuietly(conn);
            throw new JibuException(e.getMessage());
        }
    }

    public List<Setting> findByName(String name){
        Connection conn = null;
        List<Setting> settings= null;
        try {
            conn = ConnectionUtils.getConnection();
            settings = settingDAO.findByName(conn,name);
        } catch(SQLException e) {
            logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
        return settings;
    }

    public List<Setting> findByUsername(String username){
        Connection conn = null;
        List<Setting> settings= null;
        try {
            conn = ConnectionUtils.getConnection();
            settings = settingDAO.getAllDefault(conn);
            List<Setting> sets = settingDAO.findByUsername(conn,username);
            if (sets.size()==0) return settings;
            for (Setting setting:settings) {
                if (sets.contains(setting)) {
                    settings.remove(setting);
                }
            }
            settings.addAll(sets);
        } catch(SQLException e) {
            logger.error(e.getMessage());
        } finally {
            DbUtils.closeQuietly(conn);
        }
        return settings;
    }
}
