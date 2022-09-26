package com.udaychugh.utalk.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.udaychugh.utalk.R
import com.udaychugh.utalk.User


class UsersFragment : Fragment() {
    lateinit var adapter: FirestorePagingAdapter<User, RecyclerView.ViewHolder>

    private val db by lazy {
        FirebaseFirestore.getInstance().collection("users")
            .orderBy("name", Query.Direction.DESCENDING)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupAdapter()
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    private fun setupAdapter() {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(2)
            .setPageSize(10)
            .build()

        val options = FirestorePagingOptions.Builder<User>()
            .setLifecycleOwner(this)
            .setQuery(db, config, User::class.java)
            .build()

        adapter = object : FirestorePagingAdapter<User, RecyclerView.ViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {

                return UserViewHolder(
                    layoutInflater.inflate(R.layout.item_user, parent, false)
                )
            }

            override fun onBindViewHolder(
                holder: RecyclerView.ViewHolder,
                position: Int,
                model: User
            ) {
                Log.e("Recycler View Error", "$model")

                (holder as UserViewHolder).bind(model)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.userRv).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapter
        }

    }

}