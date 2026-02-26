import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../core/theme/app_theme.dart';
import '../../core/providers/app_providers.dart';
import '../widgets/island_background.dart';
import '../widgets/big_button.dart';

class ProfileSetupScreen extends ConsumerStatefulWidget {
  const ProfileSetupScreen({super.key});

  @override
  ConsumerState<ProfileSetupScreen> createState() => _ProfileSetupScreenState();
}

class _ProfileSetupScreenState extends ConsumerState<ProfileSetupScreen> {
  final _nameController = TextEditingController();
  int _selectedAge = 4;
  int _selectedAvatar = 0;

  final List<IconData> _avatars = [
    Icons.face,
    Icons.face_2,
    Icons.face_3,
    Icons.face_4,
    Icons.face_5,
    Icons.face_6,
  ];

  final List<Color> _avatarColors = [
    IslaColors.oceanBlue,
    IslaColors.palmGreen,
    IslaColors.sunsetPink,
    IslaColors.sunOrange,
    IslaColors.sunsetPurple,
    IslaColors.sunYellow,
  ];

  @override
  void dispose() {
    _nameController.dispose();
    super.dispose();
  }

  void _saveProfile() {
    if (_nameController.text.trim().isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Por favor escribe tu nombre'),
          backgroundColor: IslaColors.error,
        ),
      );
      return;
    }

    ref.read(currentProfileProvider.notifier).createProfile(
          _nameController.text.trim(),
          _selectedAge,
          avatar: _selectedAvatar.toString(),
        );

    Navigator.pushReplacementNamed(context, '/home');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: IslandBackground(
        child: SafeArea(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(24),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                const SizedBox(height: 20),
                _buildHeader(),
                const SizedBox(height: 32),
                _buildAvatarSelection(),
                const SizedBox(height: 24),
                _buildNameInput(),
                const SizedBox(height: 24),
                _buildAgeSelection(),
                const SizedBox(height: 40),
                BigButton(
                  icon: Icons.check_circle,
                  label: '¡Listo!',
                  color: IslaColors.palmGreen,
                  onPressed: _saveProfile,
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildHeader() {
    return Column(
      children: [
        Icon(
          Icons.wb_sunny,
          size: 64,
          color: IslaColors.sunYellow,
        ),
        const SizedBox(height: 16),
        Text(
          '¡Bienvenido a Isla Digital!',
          style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                color: IslaColors.oceanBlue,
                fontWeight: FontWeight.bold,
              ),
          textAlign: TextAlign.center,
        ),
        const SizedBox(height: 8),
        Text(
          'Crea tu perfil de explorador',
          style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                color: IslaColors.greyDark,
              ),
          textAlign: TextAlign.center,
        ),
      ],
    );
  }

  Widget _buildAvatarSelection() {
    return Column(
      children: [
        Text(
          'Elige tu avatar',
          style: Theme.of(context).textTheme.titleLarge?.copyWith(
                color: IslaColors.oceanDark,
                fontWeight: FontWeight.w600,
              ),
        ),
        const SizedBox(height: 16),
        Wrap(
          spacing: 16,
          runSpacing: 16,
          alignment: WrapAlignment.center,
          children: List.generate(_avatars.length, (index) {
            final isSelected = _selectedAvatar == index;
            return GestureDetector(
              onTap: () => setState(() => _selectedAvatar = index),
              child: AnimatedContainer(
                duration: const Duration(milliseconds: 200),
                width: 72,
                height: 72,
                decoration: BoxDecoration(
                  color: _avatarColors[index],
                  shape: BoxShape.circle,
                  border: Border.all(
                    color: isSelected ? IslaColors.sunYellow : Colors.transparent,
                    width: 4,
                  ),
                  boxShadow: isSelected
                      ? [
                          BoxShadow(
                            color: _avatarColors[index].withOpacity(0.5),
                            blurRadius: 12,
                            spreadRadius: 2,
                          ),
                        ]
                      : null,
                ),
                child: Icon(
                  _avatars[index],
                  size: 40,
                  color: IslaColors.white,
                ),
              ),
            );
          }),
        ),
      ],
    );
  }

  Widget _buildNameInput() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          '¿Cómo te llamas?',
          style: Theme.of(context).textTheme.titleLarge?.copyWith(
                color: IslaColors.oceanDark,
                fontWeight: FontWeight.w600,
              ),
        ),
        const SizedBox(height: 12),
        TextField(
          controller: _nameController,
          textCapitalization: TextCapitalization.words,
          style: Theme.of(context).textTheme.bodyLarge,
          decoration: InputDecoration(
            hintText: 'Escribe tu nombre aquí',
            prefixIcon: const Icon(Icons.person, color: IslaColors.oceanBlue),
            filled: true,
            fillColor: IslaColors.white,
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: IslaColors.greyLight),
            ),
            enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: IslaColors.greyLight),
            ),
            focusedBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(16),
              borderSide: const BorderSide(color: IslaColors.oceanBlue, width: 2),
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildAgeSelection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          '¿Cuántos años tienes?',
          style: Theme.of(context).textTheme.titleLarge?.copyWith(
                color: IslaColors.oceanDark,
                fontWeight: FontWeight.w600,
              ),
        ),
        const SizedBox(height: 12),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [4, 5, 6].map((age) {
            final isSelected = _selectedAge == age;
            return Padding(
              padding: const EdgeInsets.symmetric(horizontal: 8),
              child: Material(
                elevation: isSelected ? 4 : 0,
                borderRadius: BorderRadius.circular(16),
                child: InkWell(
                  onTap: () => setState(() => _selectedAge = age),
                  borderRadius: BorderRadius.circular(16),
                  child: Container(
                    width: 80,
                    height: 80,
                    decoration: BoxDecoration(
                      color: isSelected ? IslaColors.oceanBlue : IslaColors.greyLight,
                      borderRadius: BorderRadius.circular(16),
                    ),
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          '$age',
                          style: Theme.of(context).textTheme.displaySmall?.copyWith(
                                color: isSelected ? IslaColors.white : IslaColors.greyDark,
                                fontWeight: FontWeight.bold,
                              ),
                        ),
                        Text(
                          'años',
                          style: Theme.of(context).textTheme.bodySmall?.copyWith(
                                color: isSelected ? IslaColors.white.withOpacity(0.8) : IslaColors.grey,
                              ),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            );
          }).toList(),
        ),
      ],
    );
  }
}
