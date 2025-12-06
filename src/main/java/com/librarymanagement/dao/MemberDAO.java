package com.librarymanagement.dao;

import com.librarymanagement.model.Member;
import java.util.List;

/**
 * Data Access Object interface for Member entity.
 * Extends BaseDAO and adds member-specific methods.
 */
public interface MemberDAO extends BaseDAO<Member, Integer> {
    /**
     * Finds a member by user ID.
     *
     * @param userId the user ID
     * @return the Member if found, null otherwise
     * @throws Exception if query fails
     */
    Member findByUserId(Integer userId) throws Exception;

    /**
     * Gets all members.
     *
     * @return list of all members
     * @throws Exception if query fails
     */
    List<Member> findAll() throws Exception;
}

