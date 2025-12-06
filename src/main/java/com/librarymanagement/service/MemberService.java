package com.librarymanagement.service;

import com.librarymanagement.dao.MemberDAO;
import com.librarymanagement.dao.MemberDAOImpl;
import com.librarymanagement.dao.UserDAO;
import com.librarymanagement.dao.UserDAOImpl;
import com.librarymanagement.exception.AuthorizationException;
import com.librarymanagement.exception.EntityNotFoundException;
import com.librarymanagement.exception.ValidationException;
import com.librarymanagement.model.Member;
import com.librarymanagement.model.Role;
import com.librarymanagement.model.User;
import com.librarymanagement.util.InputValidator;

import java.util.List;

/**
 * Service class for member management operations.
 * Handles business logic for member CRUD operations.
 */
public class MemberService {
    private final MemberDAO memberDAO;
    private final UserDAO userDAO;

    public MemberService() {
        this.memberDAO = new MemberDAOImpl();
        this.userDAO = new UserDAOImpl();
    }

    /**
     * Constructor for dependency injection.
     *
     * @param memberDAO the MemberDAO implementation
     * @param userDAO the UserDAO implementation
     */
    public MemberService(MemberDAO memberDAO, UserDAO userDAO) {
        this.memberDAO = memberDAO;
        this.userDAO = userDAO;
    }

    /**
     * Creates a new member. Only ADMIN and LIBRARIAN can create members.
     *
     * @param currentUser the user performing the operation
     * @param userId the associated user ID
     * @param name the member name
     * @param email the member email
     * @param phone the member phone
     * @return the created Member
     * @throws AuthorizationException if current user is not authorized
     * @throws ValidationException if input is invalid
     * @throws Exception if creation fails
     */
    public Member createMember(User currentUser, Integer userId, String name, 
                               String email, String phone) 
            throws AuthorizationException, ValidationException, Exception {
        // Authorization check
        if (!isLibrarianOrAdmin(currentUser)) {
            throw new AuthorizationException("Only ADMIN and LIBRARIAN can create members");
        }

        // Validation
        if (userId == null) {
            throw new ValidationException("User ID cannot be null");
        }
        InputValidator.validateNotEmpty(name, "Name");
        if (email != null && !email.trim().isEmpty()) {
            InputValidator.validateEmail(email);
        }
        if (phone != null && !phone.trim().isEmpty()) {
            InputValidator.validatePhone(phone);
        }

        // Verify user exists and is a MEMBER
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
        if (user.getRole() != Role.MEMBER) {
            throw new ValidationException("User must have MEMBER role to be a library member");
        }

        // Check if member already exists for this user
        Member existingMember = memberDAO.findByUserId(userId);
        if (existingMember != null) {
            throw new ValidationException("Member already exists for user ID: " + userId);
        }

        // Create member
        Member member = new Member(userId, name, email, phone);
        return memberDAO.create(member);
    }

    /**
     * Gets a member by ID.
     *
     * @param memberId the member ID
     * @return the Member
     * @throws EntityNotFoundException if member not found
     * @throws Exception if query fails
     */
    public Member getMemberById(Integer memberId) throws EntityNotFoundException, Exception {
        Member member = memberDAO.findById(memberId);
        if (member == null) {
            throw new EntityNotFoundException("Member not found with ID: " + memberId);
        }
        return member;
    }

    /**
     * Gets a member by user ID.
     *
     * @param userId the user ID
     * @return the Member
     * @throws EntityNotFoundException if member not found
     * @throws Exception if query fails
     */
    public Member getMemberByUserId(Integer userId) throws EntityNotFoundException, Exception {
        Member member = memberDAO.findByUserId(userId);
        if (member == null) {
            throw new EntityNotFoundException("Member not found for user ID: " + userId);
        }
        return member;
    }

    /**
     * Gets all members. Only ADMIN and LIBRARIAN can view all members.
     *
     * @param currentUser the user performing the operation
     * @return list of all members
     * @throws AuthorizationException if current user is not authorized
     * @throws Exception if query fails
     */
    public List<Member> getAllMembers(User currentUser) throws AuthorizationException, Exception {
        if (!isLibrarianOrAdmin(currentUser)) {
            throw new AuthorizationException("Only ADMIN and LIBRARIAN can view all members");
        }
        return memberDAO.findAll();
    }

    /**
     * Updates a member. Only ADMIN and LIBRARIAN can update members.
     *
     * @param currentUser the user performing the operation
     * @param memberId the member ID to update
     * @param name the new name (can be null)
     * @param email the new email (can be null)
     * @param phone the new phone (can be null)
     * @return true if update successful
     * @throws AuthorizationException if current user is not authorized
     * @throws EntityNotFoundException if member not found
     * @throws ValidationException if input is invalid
     * @throws Exception if update fails
     */
    public boolean updateMember(User currentUser, Integer memberId, String name, 
                               String email, String phone) 
            throws AuthorizationException, EntityNotFoundException, ValidationException, Exception {
        if (!isLibrarianOrAdmin(currentUser)) {
            throw new AuthorizationException("Only ADMIN and LIBRARIAN can update members");
        }

        Member member = memberDAO.findById(memberId);
        if (member == null) {
            throw new EntityNotFoundException("Member not found with ID: " + memberId);
        }

        // Update fields if provided
        if (name != null && !name.trim().isEmpty()) {
            member.setName(name.trim());
        }

        if (email != null && !email.trim().isEmpty()) {
            InputValidator.validateEmail(email);
            member.setEmail(email.trim());
        }

        if (phone != null && !phone.trim().isEmpty()) {
            InputValidator.validatePhone(phone);
            member.setPhone(phone.trim());
        }

        return memberDAO.update(member);
    }

    /**
     * Deletes a member. Only ADMIN and LIBRARIAN can delete members.
     *
     * @param currentUser the user performing the operation
     * @param memberId the member ID to delete
     * @return true if deletion successful
     * @throws AuthorizationException if current user is not authorized
     * @throws EntityNotFoundException if member not found
     * @throws Exception if deletion fails
     */
    public boolean deleteMember(User currentUser, Integer memberId) 
            throws AuthorizationException, EntityNotFoundException, Exception {
        if (!isLibrarianOrAdmin(currentUser)) {
            throw new AuthorizationException("Only ADMIN and LIBRARIAN can delete members");
        }

        Member member = memberDAO.findById(memberId);
        if (member == null) {
            throw new EntityNotFoundException("Member not found with ID: " + memberId);
        }

        return memberDAO.delete(memberId);
    }

    /**
     * Helper method to check if user is librarian or admin.
     */
    private boolean isLibrarianOrAdmin(User user) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        return user.getRole() == Role.LIBRARIAN || user.getRole() == Role.ADMIN;
    }
}

