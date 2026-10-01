# Windows publisher identity

The Windows release pipeline supports Authenticode signing. Configure the GitHub Actions secrets WINDOWS_CERTIFICATE_BASE64 and WINDOWS_CERTIFICATE_PASSWORD with a trusted PFX code-signing certificate to replace **Unknown publisher** with the verified publisher name.

The installer is signed with SHA-256 and a trusted timestamp. Never commit the PFX file or its password.
