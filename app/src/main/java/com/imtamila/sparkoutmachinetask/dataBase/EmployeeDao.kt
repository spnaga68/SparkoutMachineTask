package com.imtamila.sparkoutmachinetask.dataBase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.imtamila.sparkoutmachinetask.employees.data.ContactData
import com.imtamila.sparkoutmachinetask.employees.data.EmployeeResponseData


@Dao
interface EmployeeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg employeeResponseData: EmployeeResponseData)

    @Query("SELECT * FROM employees_list")
    fun loadEmployees(): LiveData<EmployeeResponseData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllContacts(contactsList: List<ContactData>)

    @Query("SELECT * FROM contacts_list")
    fun loadAllContacts(): LiveData<List<ContactData>>

    @Query("SELECT * FROM contacts_list WHERE isFavourite == 1")
    fun loadAllFavoriteContacts(): LiveData<List<ContactData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(vararg contactData: ContactData)

    @Query("SELECT * FROM contacts_list WHERE contactName LIKE '%' || :constraint  || '%' OR contactNumber LIKE '%' || :constraint  || '%'")
    fun searchContacts(constraint: String): LiveData<List<ContactData>>

    @Query("UPDATE contacts_list SET isFavourite = :isFavourite WHERE contactName = :name AND contactNumber = :phone")
    fun updateFavorite(name: String, phone: String, isFavourite: Int)
}