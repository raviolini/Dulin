#version 330 core

layout (location = 0) in vec3 attr_position;
layout (location = 1) in vec4 attr_color;
layout (location = 2) in vec2 attr_texCoord;

out vec4 out_color;
out vec2 out_texCoord;

void main()
{
        gl_Position = vec4(attr_position, 1.0);
        out_color = attr_color;
        out_texCoord = attr_texCoord;
}

