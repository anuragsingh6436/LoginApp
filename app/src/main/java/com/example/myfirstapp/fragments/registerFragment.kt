package com.example.myfirstapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myfirstapp.R
import com.example.myfirstapp.Room.UserEntity
import com.example.myfirstapp.activities.UsersListActivity
import com.example.myfirstapp.databinding.FragmentRegisterBinding
import com.example.myfirstapp.helpers.InputValidation
import com.example.myfirstapp.viewmodel.UserViewModelRegister
import com.google.android.material.snackbar.Snackbar

class registerFragment : Fragment(),View.OnClickListener {
    lateinit var binding: FragmentRegisterBinding
    private val activity = this
    private lateinit var inputValidation: InputValidation
    private lateinit var userViewModelRegister: UserViewModelRegister
    private var gender = "male"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userViewModelRegister = ViewModelProvider(this).get(UserViewModelRegister::class.java)
        inputValidation = context?.let { InputValidation(it) }!!
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_register, container, false);
        userViewModelRegister.checkUser.observe(viewLifecycleOwner, Observer { users ->
            Log.d("fragment","checkUser")
            users?.let {
                if (it) {
                    Snackbar.make(
                        binding.nestedScrollView,
                        getString(R.string.error_email_exists),
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    postRoomDataBase()
                }
            }
        })
        binding.appCompatTextViewLoginLink.setOnClickListener(this)
        binding.appCompatButtonRegister.setOnClickListener(this)
        binding.radioButton.setOnClickListener(this)
        binding.maleRadio.setOnClickListener(this)
        binding.femaleRadio.setOnClickListener(this)
        return binding.root
    }
    private fun postDataToSQLite() {
        if (!inputValidation.isInputEditTextFilled(binding.textInputEditTextName, binding.textInputLayoutName, getString(R.string.error_message_name))) {
            return
        }
        if (!inputValidation.isInputEditTextFilled(binding.textInputEditTextEmail, binding.textInputLayoutEmail, getString(R.string.error_message_email))) {
            return
        }
        if (!inputValidation.isInputEditTextEmail(binding.textInputEditTextEmail, binding.textInputLayoutEmail, getString(R.string.error_message_email))) {
            return
        }
        if (!inputValidation.isInputEditTextFilled(binding.textInputEditTextPassword, binding.textInputLayoutPassword, getString(R.string.error_message_password))) {
            return
        }
        if (!inputValidation.isInputEditTextMatches(binding.textInputEditTextPassword, binding.textInputEditTextConfirmPassword,
                binding.textInputLayoutConfirmPassword, getString(R.string.error_password_match))) {
            return
        }
        if(!inputValidation.isInputEditTextFilled(binding.textInputEditTextCity,binding.textInputLayoutCity,getString(R.string.error_message_city))) {
            return
        }
        var inputEmail = binding.textInputEditTextEmail.text.toString()

        userViewModelRegister.findByEmail(inputEmail)
    }

    private fun postRoomDataBase() {
        var userRoom = UserEntity(1,binding.textInputEditTextName.text.toString().trim(),
            email = binding.textInputEditTextEmail.text.toString().trim(),
            password = binding.textInputEditTextPassword.text.toString().trim(),
            city = binding.textInputEditTextCity.text.toString().trim(),
            gender = gender);
        userViewModelRegister.insert(userRoom)
        Snackbar.make(binding.nestedScrollView, getString(R.string.success_message), Snackbar.LENGTH_LONG).show()
        var bundle = Bundle()
        bundle.putString("email",binding.textInputEditTextEmail.text.toString().trim { it <= ' ' })
        emptyInputEditText()
        val accountsIntent = Intent(context, UsersListActivity::class.java)
        accountsIntent.putExtras(bundle)
        startActivity(accountsIntent)
    }

    private fun emptyInputEditText() {
        binding.textInputEditTextName.text = null
        binding.textInputEditTextEmail.text = null
        binding.textInputEditTextPassword.text = null
        binding.textInputEditTextConfirmPassword.text = null
        binding.textInputEditTextCity.text  = null
    }
//    private fun onRadioButtonClicked(view:View) {
//        Log.d("fragment","inside radio Button")
//        if(view is RadioButton) {
//            val checked = view.isChecked
//            when(view.getId()) {
//                R.id.maleRadio ->
//                    if(checked) {
//                        gender = "male"
//                    }
//                R.id.femaleRadio -> {
//                    if(checked) {
//                        gender = "female"
//                    }
//                }
//            }
//        }
//    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.appCompatButtonRegister -> postDataToSQLite()
                R.id.appCompatTextViewLoginLink -> {
                    val fragment: Fragment = loginFragment()
                    val fragmentManager: FragmentManager = getActivity()!!.supportFragmentManager
                    val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.fragment_container, fragment)
                    fragmentTransaction.commit()
                }
                R.id.maleRadio -> {
                    Log.d("fragment","male")
                    gender = "male"
                }
                R.id.femaleRadio -> {
                    Log.d("fragment","female")
                    gender = "female"
                }
            }
        }
    }
}
