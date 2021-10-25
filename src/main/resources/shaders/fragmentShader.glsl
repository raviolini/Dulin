#version 330 core

in vec4 out_color;
in vec2 out_texCoord;

out vec4 FragColor;

uniform sampler2D uni_texture;

void main()
{
    FragColor = texture(uni_texture, out_texCoord) * out_color;
}
