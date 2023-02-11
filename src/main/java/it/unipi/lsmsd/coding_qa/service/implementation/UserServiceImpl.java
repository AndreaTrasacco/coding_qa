package it.unipi.lsmsd.coding_qa.service.implementation;

import it.unipi.lsmsd.coding_qa.dao.*;
import it.unipi.lsmsd.coding_qa.dao.enums.DAORepositoryEnum;
import it.unipi.lsmsd.coding_qa.dao.exception.DAONodeException;
import it.unipi.lsmsd.coding_qa.dto.PageDTO;
import it.unipi.lsmsd.coding_qa.dto.UserDTO;
import it.unipi.lsmsd.coding_qa.dto.UserRegistrationDTO;
import it.unipi.lsmsd.coding_qa.model.RegisteredUser;
import it.unipi.lsmsd.coding_qa.model.User;
import it.unipi.lsmsd.coding_qa.service.UserService;
import it.unipi.lsmsd.coding_qa.service.exception.BusinessException;

import java.util.Date;

public class UserServiceImpl implements UserService {

    private UserDAO userDAO;
    private UserNodeDAO userNodeDAO;

    public UserServiceImpl() {
        this.userDAO = DAOLocator.getUserDAO(DAORepositoryEnum.MONGODB);
        this.userNodeDAO = DAOLocator.getUserNodeDAO(DAORepositoryEnum.NEO4J);
    }

    public UserDTO register(UserRegistrationDTO user) throws BusinessException {
        RegisteredUser registeredUser = new RegisteredUser();
        try {
            registeredUser.setNickname(user.getNickname());
            registeredUser.setFullName(user.getFullName());
            registeredUser.setCountry(user.getCountry());
            registeredUser.setBirthdate(user.getBirthdate());
            registeredUser.setWebsite(user.getWebsite());
            registeredUser.setCreatedDate(new Date(System.currentTimeMillis()));
            registeredUser.setEncPassword(user.getEncPassword());
            registeredUser.setScore(0);

            userDAO.register(registeredUser);
            userNodeDAO.create(registeredUser.getNickname());

            UserDTO userDTO = new UserDTO();
            userDTO.setId(registeredUser.getId());
            userDTO.setBirthdate(registeredUser.getBirthdate());
            userDTO.setCountry(registeredUser.getCountry());
            userDTO.setNickname(registeredUser.getNickname());
            userDTO.setScore(registeredUser.getScore());
            userDTO.setWebsite(registeredUser.getCountry());
            userDTO.setFullName(registeredUser.getNickname());
            userDTO.setCreatedDate(registeredUser.getCreatedDate());

            return userDTO;
        } catch (DAONodeException ex) {
            try {
                userDAO.delete(registeredUser.getId());
            } catch (Exception e) {
                throw new BusinessException(e);
            }
            throw new BusinessException(ex);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    public UserDTO login(String username, String encPassword) throws BusinessException {
        try {
            User user = userDAO.authenticate(username, encPassword);
            if (user == null)
                return null;
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setFullName(user.getFullName());
            userDTO.setNickname(user.getNickname());
            if (user instanceof RegisteredUser) {
                userDTO.setScore(((RegisteredUser) user).getScore());
                userDTO.setCountry(((RegisteredUser) user).getCountry());
                userDTO.setBirthdate(((RegisteredUser) user).getBirthdate());
                userDTO.setWebsite(((RegisteredUser) user).getWebsite());
                userDTO.setCreatedDate(((RegisteredUser) user).getCreatedDate());
            }
            return userDTO;
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    public UserDTO getInfo(String nickname) throws BusinessException {
        try {
            return userDAO.getInfo(nickname);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    public void updateInfo(UserRegistrationDTO userRegistrationDTO) throws BusinessException {
        try {
            RegisteredUser registeredUser = new RegisteredUser();
            registeredUser.setCountry(userRegistrationDTO.getCountry());
            registeredUser.setBirthdate(userRegistrationDTO.getBirthdate());
            registeredUser.setWebsite(userRegistrationDTO.getWebsite());
            registeredUser.setEncPassword(userRegistrationDTO.getEncPassword());
            registeredUser.setFullName(userRegistrationDTO.getFullName());
            userDAO.updateInfo(registeredUser);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    public void follow(String myself, String userToFollow) throws BusinessException {
        try {
            userNodeDAO.followUser(myself, userToFollow);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    public void unfollow(String myself, String userToUnFollow) throws BusinessException {
        try {
            userNodeDAO.deleteFollowed(myself, userToUnFollow);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    public void delete(String id, String nickname) throws BusinessException {
        try {
            userDAO.delete(id);
            userNodeDAO.delete(nickname);
        } catch (DAONodeException ex) { // If there is a failure in the deletion of node User --> retry deletion
            try {
                userNodeDAO.delete(nickname);
            } catch (Exception e) {
                throw new BusinessException(e);
            }
            throw new BusinessException(ex);
        } catch (Exception ex) {
            throw new BusinessException(ex);
        }
    }

    public PageDTO<String> getFollowerList(String nickname) throws BusinessException {
        try {
            return userNodeDAO.getFollowingList(nickname);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }

    @Override
    public int getScore(String userId) throws BusinessException {
        try {
            return userDAO.getScore(userId);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
    }
}
