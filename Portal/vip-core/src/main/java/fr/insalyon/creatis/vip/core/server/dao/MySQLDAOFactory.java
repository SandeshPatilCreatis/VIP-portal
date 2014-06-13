/* Copyright CNRS-CREATIS
 *
 * Rafael Ferreira da Silva
 * rafael.silva@creatis.insa-lyon.fr
 * http://www.rafaelsilva.com
 *
 * This software is a grid-enabled data-driven workflow manager and editor.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package fr.insalyon.creatis.vip.core.server.dao;

import fr.insalyon.creatis.vip.core.server.dao.mysql.AccountData;
import fr.insalyon.creatis.vip.core.server.dao.mysql.GroupData;
import fr.insalyon.creatis.vip.core.server.dao.mysql.PublicationData;
import fr.insalyon.creatis.vip.core.server.dao.mysql.UserData;
import fr.insalyon.creatis.vip.core.server.dao.mysql.UsersGroupsData;

/**
 *
 * @author Rafael Ferreira da Silva, Nouha Boujelben
 */
public class MySQLDAOFactory extends CoreDAOFactory {

    private static CoreDAOFactory instance;

    // Singleton
    protected static CoreDAOFactory getInstance() {
        if (instance == null) {
            instance = new MySQLDAOFactory();
        }
        return instance;
    }

    private MySQLDAOFactory() {
    }

    @Override
    public UserDAO getUserDAO() throws DAOException {
        return new UserData();
    }

    @Override
    public GroupDAO getGroupDAO() throws DAOException {
        return new GroupData();
    }

    @Override
    public UsersGroupsDAO getUsersGroupsDAO() throws DAOException {
        return new UsersGroupsData();
    }

    @Override
    public AccountDAO getAccountDAO() throws DAOException {
        return new AccountData();
    }

    @Override
    public PublicationDAO getPublicationDAO() throws DAOException {
        return new PublicationData();
    }
}
