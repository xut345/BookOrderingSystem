package com.example.ding.umutos.persistence.hsqldb;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.ding.umutos.persistence.OrderPersistence;
import com.example.ding.umutos.objects.*;


public class OrderPersistenceHSQLDB  implements OrderPersistence{

    private final String dbPath;

    public OrderPersistenceHSQLDB(final String dbPath)
    {
        this.dbPath = dbPath;
    }

    private Connection connection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:file:" + dbPath + ";shutdown=true","SA","");
    }

    private Order fromResultSet(final ResultSet rs) throws SQLException {
        final String bookName = rs.getString("bookName");
        final int buyerID = rs.getInt("buyerID");
        final int sellerID = rs.getInt("sellerID");
        final double price = rs.getDouble("price");
        final String buyerFirstName = rs.getString("buyerFirstName");
        final String buyerLastName = rs.getString("buyerLastName");
        final String postCode = rs.getString("postCode");
        final String phoneNumber = rs.getString("phoneNumber");
        final String address = rs.getString("address");
        final String[] orderInfo = {buyerFirstName,buyerLastName,postCode,phoneNumber,address};

        return new Order(bookName,buyerID,sellerID,price,orderInfo);
    }


    @Override
    public Order insertOrder(Order currentOrder) {
        try(final Connection c = connection()){
            final PreparedStatement st = c.prepareStatement("INSERT INTO orders VALUES(?,?,?,?,?,?,?,?,?)");
            st.setString(1,currentOrder.getBookName());
            st.setInt(2,currentOrder.getBuyerID());
            st.setInt(3,currentOrder.getSellerID());
            st.setDouble(4,currentOrder.getPrice());
            st.setString(5,currentOrder.getBuyerFirstName());
            st.setString(6,currentOrder.getBuyerLastName());
            st.setString(7,currentOrder.getPostCode());
            st.setString(8,currentOrder.getPhoneNumber());
            st.setString(9,currentOrder.getAddress());

            st.executeUpdate();

            return currentOrder;
        } catch (final SQLException e){
            throw new PersistenceException(e);
        }
    }

    @Override
    public List<Order> getBuyerOrders(int userID) {
        final List<Order> orders = new ArrayList<>();
        try(final Connection c = connection()){
            final PreparedStatement st = c.prepareStatement("SELECT * FROM orders WHERE buyerID = ?");
            st.setInt(1, userID);
            final ResultSet rs = st.executeQuery();
            while(rs.next()){
                final Order order = fromResultSet(rs);
                orders.add(order);
            }
            rs.close();
            st.close();

            return orders;

        } catch (final SQLException e){
            throw new PersistenceException(e);
        }
    }

    @Override
    public List<Order> getSellerOrders(int userID) {
        final List<Order> orders = new ArrayList<>();
        try(final Connection c = connection()){
            final PreparedStatement st = c.prepareStatement("SELECT * FROM orders WHERE sellerID = ?");
            st.setInt(1, userID);
            final ResultSet rs = st.executeQuery();
            while(rs.next()){
                final Order order = fromResultSet(rs);
                orders.add(order);
            }
            rs.close();
            st.close();

            return orders;

        } catch (final SQLException e){
            throw new PersistenceException(e);
        }
    }

    @Override
    public List<Order> getOrders() {
        return null;
    }

}