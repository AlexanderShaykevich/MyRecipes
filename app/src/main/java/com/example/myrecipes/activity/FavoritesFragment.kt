package com.example.myrecipes.activity

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.myrecipes.R
import com.example.myrecipes.databinding.FragmentFavoritesBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.myrecipes.data.RecipeAdapter
import com.example.myrecipes.data.RecipeViewModel

class FavoritesFragment : Fragment() {
    lateinit var binding: FragmentFavoritesBinding
    private val viewModel by viewModels<RecipeViewModel>(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val adapter = RecipeAdapter(requireContext(),viewModel)
        binding.recyclerView.adapter = adapter
        requireActivity().title = getString(R.string.favorites)

        viewModel.data.observe(viewLifecycleOwner) { recipes ->
            adapter.submitList(recipes.filter { it.inFavorites })
            binding.emptyTextGroup.isVisible = recipes.none { it.inFavorites }
        }

        viewModel.openRecipeEvent.observe(viewLifecycleOwner) { id ->
            findNavController().navigate(
                R.id.action_favorites_to_recipeFragment,
                Bundle().apply {
                    putLong(MainFragment.KEY_ID, id)
                })
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_favorites, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                return when (menuItem.itemId) {
                    R.id.action_back -> {
                        findNavController().navigateUp()
                        true
                    }

                    R.id.deleteAll -> {
                        if(binding.emptyTextGroup.isVisible) {
                            return false
                        } else {
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle(R.string.confirm_remove)
                                .setNegativeButton(R.string.cancel) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .setPositiveButton(R.string.OK) { dialog, _ ->
                                    viewModel.onDeleteAllFromFavoritesListener()
                                    dialog.dismiss()
                                }
                                .show()
                            true
                        }
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }


}