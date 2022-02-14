package edu.rosehulman.chronic.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import edu.rosehulman.chronic.adapters.PostAdapter
import edu.rosehulman.chronic.databinding.FragmentForumHomeBinding
import edu.rosehulman.chronic.databinding.FragmentLoadingBinding

class GlobalForumFragment: Fragment() {
    private lateinit var binding: FragmentForumHomeBinding
    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        binding = FragmentForumHomeBinding.inflate(inflater,container,false)

        adapter = PostAdapter(this)
        binding.postRecyclerView.adapter = adapter
        adapter.addListener(fragmentName, "Time")
        binding.postRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.postRecyclerView.setHasFixedSize(true)

        binding.forumAddFab.setOnClickListener {

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.removeListener(fragmentName)
    }

    companion object {
        const val fragmentName = "GlobalForumFragment"
    }
}